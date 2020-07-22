/*
 * MIT License
 *
 * Copyright (c) 2020 Diego Urrutia-Astorga <durrutia@ucn.cl>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package cl.ucn.disc.pdis.parking;

import cl.ucn.disc.pdis.parking.ZeroIce.Model.Persona;
import cl.ucn.disc.pdis.parking.ZeroIce.Model.Sexo;
import cl.ucn.disc.pdis.parking.dao.Repository;
import cl.ucn.disc.pdis.parking.dao.RepositoryOrmLite;
import cl.ucn.disc.pdis.parking.model.Funcionario;
import cl.ucn.disc.pdis.parking.scrappers.DirectorioUCN;
import cl.ucn.disc.pdis.parking.scrappers.NombreRutFirma;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zeroc.Ice.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The Main class to scrape the agenda UCN.
 *
 * @author Diego Urrutia-Astorga.
 */
@Slf4j
public final class Main {

    /**
     * The entry point.
     *
     * @param args to use.
     * @throws IOException if any error.
     */
    public static void main(final String[] args) throws IOException {

        // from .. to ..
        final int ini = 26300;
        final int end = 40000;

        // The encryption key to use
        // noinspection CallToSystemGetenv
        final String dbkey = System.getenv("DB_KEY");
        log.info("Using DBKEY={} as encryption key.", dbkey);

        // The database to use
        // final String jdbc = "jdbc:sqlite:file:funcionarios.db?cipher=chacha20&key=" + dbkey;
        final String jdbc = "jdbc:sqlite:file:funcionarios.db";

        // Connection to the database
        try (ConnectionSource cs = new JdbcConnectionSource(jdbc)) {

            // The repo of Funcionario
            final Repository<Funcionario, Long> repo = new RepositoryOrmLite<>(cs, Funcionario.class);

            log.debug("Getting the data from {} to {} ...", ini, end);
            for (int codigo = ini; codigo <= end; codigo++) {

                // Get the funcionario from the backend
                final Funcionario funcionario = getOrScrape(codigo, repo);
                if (funcionario.getStatus() == Funcionario.Status.UCN_SCRAPED) {

                    // Sleeping
                    sleep();
                    sleep();
                    sleep();
                    sleep();
                    sleep();

                    // Scrapping
                    final List<NombreRutFirma.Rutificador> rutificadors = NombreRutFirma.scrape(funcionario.getNombre());

                    // 1. Not found!
                    if (rutificadors.isEmpty()) {
                        log.warn("Rutificador {} not found.", funcionario.getNombre());
                        funcionario.setStatus(Funcionario.Status.NRF_NOTFOUND);
                    }

                    // 2. Found exactly 1 record
                    if (rutificadors.size() == 1) {
                        final NombreRutFirma.Rutificador rutificador = rutificadors.get(0);
                        log.info("Rutificador {} successful: {} !!", funcionario.getNombre(), rutificador.getRut());
                        funcionario.setRut(rutificador.getRut());
                        funcionario.setSexo(rutificador.getSexo().equalsIgnoreCase("VAR") ? Funcionario.Sexo.MASCULINO : Funcionario.Sexo.FEMENINO);
                        funcionario.setDireccion(rutificador.getDireccion());
                        funcionario.setComuna(rutificador.getComuna());
                        funcionario.setStatus(Funcionario.Status.NRF_SCRAPED);
                    }

                    // 3. Found more than 1 record
                    if (rutificadors.size() > 1) {
                        log.warn("Rutificador {} more than one data founded!", funcionario.getNombre());
                        for (NombreRutFirma.Rutificador rutificador : rutificadors) {
                            log.warn("* Rutificador: {} --> {}", rutificador.getRut(), rutificador.getNombre());
                        }
                        funcionario.setStatus(Funcionario.Status.NRF_MANY);
                    }

                    // Save into the backend
                    repo.update(funcionario);

                    final Persona persona = toPersona(funcionario);
                    log.debug("Persona: {}", ToStringBuilder.reflectionToString(persona, ToStringStyle.MULTI_LINE_STYLE));
                    List<Persona> personas = Collections.singletonList(persona);
                    saveIntoBackend(personas);

                }

            }

        } catch (SQLException ex) {
            log.error("Error", ex);
        }

        log.debug("Done.");

    }

    /**
     * Get or scrap Funcionario from codigo.
     *
     * @param codigo  to get/scrape.
     * @param theRepo used to connect.
     * @return the {@link Funcionario}.
     */
    private static Funcionario getOrScrape(final int codigo, final Repository<Funcionario, Long> theRepo) {

        // Get the Funcionario from backend
        final List<Funcionario> funcionarios = theRepo.findAll("codigo", codigo);
        final Funcionario funcionario = funcionarios.isEmpty() ? null : funcionarios.get(0);

        // Found funcionario
        if (funcionario != null) {
            return funcionario;
        }

        // Not found in the database
        log.debug("Can't find Funcionario with codigo {} in the backend, scrapping ..", codigo);

        // Just wait ..
        sleep();

        // Get the ficha ..
        final DirectorioUCN.Ficha ficha = DirectorioUCN.scrape(codigo);

        // If ficha null, insert Funcionario.Status.NOTFOUND
        if (ficha == null) {
            log.warn("Can't find Ficha {} in DirectorioUCN, skipping ..", codigo);

            // Not found, save as UCN_NOTFOUND
            final Funcionario p = Funcionario.builder()
                    .codigo(codigo)
                    .status(Funcionario.Status.UCN_NOTFOUND)
                    .build();

            theRepo.create(p);

            return p;
        }

        // Founded: save as UCN_SCRAPED
        final Funcionario p = Funcionario.builder()
                .codigo(codigo)
                .nombre(ficha.getNombre())
                .cargo(ficha.getCargo())
                .unidad(ficha.getUnidad())
                .email(ficha.getEmail())
                .telefonoFijo(ficha.getTelefono())
                .oficina(ficha.getOficina())
                .direccion(ficha.getDireccion())
                .status(Funcionario.Status.UCN_SCRAPED)
                .build();

        // Save!
        theRepo.create(p);

        return p;

    }

    /**
     * Just wait.
     */
    private static void sleep() {

        // Milliseconds
        int base = 2000;
        int max = 3000;
        int sleep = base + RandomUtils.nextInt(0, max + 1);
        log.trace("Sleeping for {}ms.", sleep);

        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            // Nothing here
        }
    }

    /**
     * Convert a {@link Funcionario} to {@link Persona}.
     *
     * @param funcionario to use.
     * @return the Persona.
     */
    public static Persona toPersona(final Funcionario funcionario) {

        // The Persona of ZeroIce
        final Persona persona = new Persona();
        // + properties from zeroice
        persona.codigo = funcionario.getCodigo();
        // Remove . and -
        persona.rut = StringUtils.remove(StringUtils.remove(funcionario.getRut(), "."), "-");
        persona.nombre = funcionario.getNombre();
        persona.email = funcionario.getEmail();
        persona.cargo = funcionario.getCargo();
        persona.unidad = funcionario.getUnidad();
        persona.fijo = funcionario.getTelefonoFijo();
        if (funcionario.getSexo() != null) {
            persona.sexo = funcionario.getSexo() == Funcionario.Sexo.MASCULINO ? Sexo.MASCULINO : Sexo.FEMENINO;
        }

        return persona;
    }

    /**
     * Send the List of {@link Persona} to the backend.
     *
     * @param personas to send.
     */
    private static void saveIntoBackend(final List<Persona> personas) {

        // Properties
        final Properties properties = Util.createProperties();
        // properties.setProperty("Ice.Package.model", "cl.ucn.disc.pdis.parking.ZeroIce.Model");

        // https://doc.zeroc.com/ice/latest/property-reference/ice-trace
        properties.setProperty("Ice.Trace.Admin.Properties", "1");
        properties.setProperty("Ice.Trace.Locator", "2");
        properties.setProperty("Ice.Trace.Network", "3");
        properties.setProperty("Ice.Trace.Protocol", "1");
        properties.setProperty("Ice.Trace.Slicing", "1");
        properties.setProperty("Ice.Trace.ThreadPool", "1");
        properties.setProperty("Ice.Compression.Level", "9");
        // properties.setProperty("Ice.Plugin.Slf4jLogger.java", "cl.ucn.disc.pdis.fivet.zeroice.Slf4jLoggerPluginFactory");

        InitializationData initializationData = new InitializationData();
        initializationData.properties = properties;

        // The communicator
        try (Communicator communicator = Util.initialize(initializationData)) {

            // The name
            final String name = cl.ucn.disc.pdis.parking.ZeroIce.Services.Repository.class.getName();
            log.debug("Proxying <{}> ..", name);

            ObjectPrx theProxy = communicator.stringToProxy(name + ":tcp -z -t 15000 -p 8080");
            cl.ucn.disc.pdis.parking.ZeroIce.Services.RepositoryPrx theRepo = cl.ucn.disc.pdis.parking.ZeroIce.Services.RepositoryPrx.checkedCast(theProxy);

            if (theRepo == null) {
                throw new IllegalStateException("Invalid RepositoryPrx! (wrong proxy?)");
            }

            for (final Persona persona : personas) {
                log.debug("Sending Persona {} -> {}.", persona.codigo, persona.nombre);
                theRepo.save(persona);
            }
        } catch (ConnectionRefusedException ex) {
            log.warn("Backend error", ex);
        }

    }

}
