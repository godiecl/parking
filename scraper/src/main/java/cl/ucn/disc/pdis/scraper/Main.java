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

package cl.ucn.disc.pdis.scraper;

import cl.ucn.disc.pdis.scraper.dao.Repository;
import cl.ucn.disc.pdis.scraper.dao.RepositoryOrmLite;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.sql.SQLException;
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
        final int ini = 1;
        final int end = 40000;

        // The encryption key to use
        // noinspection CallToSystemGetenv
        final String dbkey = System.getenv("DB_KEY");
        log.info("Using DBKEY={} as encryption key.", dbkey);

        // The database to use
        final String jdbc = "jdbc:sqlite:file:personas.db?cipher=chacha20&key=" + dbkey;

        // Connection to the database
        try (ConnectionSource cs = new JdbcConnectionSource(jdbc)) {

            // The repo of Persona
            final Repository<Persona, Long> repo = new RepositoryOrmLite<>(cs, Persona.class);

            log.debug("Getting the data from {} to {} ...", ini, end);
            for (int codigo = ini; codigo <= end; codigo++) {

                // Get the persona from the backend
                final Persona persona = getOrScrape(codigo, repo);
                if (persona.getStatus() == Persona.Status.UCN_SCRAPED) {

                    // Sleeping
                    sleep();

                    // Scrapping
                    final List<NombreRutFirma.Rutificador> rutificadors = NombreRutFirma.scrape(persona.getNombre());

                    // 1. Not found!
                    if (rutificadors.isEmpty()) {
                        log.warn("Rutificador {} not found.", persona.getNombre());
                        persona.setStatus(Persona.Status.NRF_NOTFOUND);
                    }

                    // 2. Found exactly 1 record
                    if (rutificadors.size() == 1) {
                        log.info("Rutificador {} successful!", persona.getNombre());
                        final NombreRutFirma.Rutificador rutificador = rutificadors.get(0);
                        persona.setRut(rutificador.getRut());
                        persona.setSexo(rutificador.getSexo().equalsIgnoreCase("VAR") ? Persona.Sexo.MASCULINO : Persona.Sexo.FEMENINO);
                        persona.setDireccion(rutificador.getDireccion());
                        persona.setComuna(rutificador.getComuna());
                        persona.setStatus(Persona.Status.NRF_SCRAPED);
                    }

                    // 3. Found more than 1 record
                    if (rutificadors.size() > 1) {
                        log.warn("Rutificador {} more than one data founded!", persona.getNombre());
                        persona.setStatus(Persona.Status.NRF_MANY);
                    }

                    // Save into the backend
                    repo.update(persona);
                }

            }

        } catch (SQLException ex) {
            log.error("Error", ex);
        }

        log.debug("Done.");

    }

    /**
     * Get or scrap Persona from codigo.
     *
     * @param codigo  to get/scrape.
     * @param theRepo used to connect.
     * @return the {@link Persona}.
     */
    private static Persona getOrScrape(final int codigo, final Repository<Persona, Long> theRepo) {

        // Get the Persona from backend
        final List<Persona> personas = theRepo.findAll("codigo", codigo);
        final Persona persona = personas.isEmpty() ? null : personas.get(0);

        // Found persona
        if (persona != null) {
            return persona;
        }

        // Not found in the database
        log.debug("Can't find Persona with codigo {} in the backend, scrapping ..", codigo);

        // Just wait ..
        sleep();

        // Get the ficha ..
        final DirectorioUCN.Ficha ficha = DirectorioUCN.scrape(codigo);

        // If ficha null, insert Persona.Status.NOTFOUND
        if (ficha == null) {
            log.warn("Can't find Ficha {} in DirectorioUCN, skipping ..", codigo);

            // Not found, save as UCN_NOTFOUND
            final Persona p = Persona.builder()
                    .codigo(codigo)
                    .status(Persona.Status.UCN_NOTFOUND)
                    .build();

            theRepo.create(p);

            return p;
        }

        // Founded: save as UCN_SCRAPED
        final Persona p = Persona.builder()
                .codigo(codigo)
                .nombre(ficha.getNombre())
                .cargo(ficha.getCargo())
                .unidad(ficha.getUnidad())
                .email(ficha.getEmail())
                .telefonoFijo(ficha.getTelefono())
                .oficina(ficha.getOficina())
                .direccion(ficha.getDireccion())
                .status(Persona.Status.UCN_SCRAPED)
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
        int sleep = base + RandomUtils.nextInt(0,max + 1);
        log.trace("Sleeping for {}ms.", sleep);

        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            // Nothing here
        }
    }

}
