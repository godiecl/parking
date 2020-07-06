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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class.
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
    @SuppressWarnings("LawOfDemeter")
    public static void main(String[] args) throws IOException {

        // from .. to ..
        final int ini = 21;
        final int end = 40; // 40000;

        // Contenedor of Personas
        final List<Persona> personas = new ArrayList<>(100);

        log.debug("Getting the data from {} to {} ...", ini, end);
        for (int id = ini; id <= end; id++) {

            log.debug("Testing for id {} ...", id);

            // Just wait
            sleep();

            // The HTML document
            final Document document = Jsoup.connect(
                    "http://online.ucn.cl/directoriotelefonicoemail/fichaGenerica/?cod=" + id)
                    .get();

            // Trying to get the nombre
            final String nombre = getText(document, "lblNombre");
            if (nombre == null) {
                log.debug("Name not found in id {}, skipping !!", id);
                // Not found, skipping!
                continue;
            }

            // All the data ..
            final String cargo = getText(document, "lblCargo");
            final String unidad = getText(document, "lblUnidad");
            final String email = getText(document, "lblEmail");
            final String telefono = getText(document, "lblTelefono");
            final String oficina = getText(document, "lblOficina");
            final String direccion = getText(document, "lblDireccion");

            // Create a new persona.
            final Persona persona = Persona.builder()
                    .key(id)
                    .nombre(nombre)
                    .cargo(cargo)
                    .unidad(unidad)
                    .email(email)
                    .telefonoFijo(telefono)
                    .oficina(oficina)
                    .build();

            // Insert into the list
            personas.add(persona);

        }

        log.debug("Personas founded: {}", personas.size());

        log.debug("Saving in json ..");

        // Gson to use to convert List<Persona> to JSON.
        final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        // Write in a file
        try (final Writer writer = Files.newBufferedWriter(Paths.get("personas.json"))) {
            gson.toJson(personas, writer);
        }

        log.debug("Done.");

    }

    /**
     * @param document to use.
     * @param id       to find.
     * @return the value.
     */
    @Nullable
    private static String getText(final Document document, final String id) {

        final Element element = document.getElementById(id);
        if (element == null) {
            return null;
        }

        final String text = element.text();
        if (StringUtils.isEmpty(text)) {
            return null;
        }

        final String value = StringUtils.trim(text);
        log.debug("{} -> {}", id, value);
        return value;

    }

    /**
     * Just wait.
     */
    private static void sleep() {
        try {
            Thread.sleep(1000 + RandomUtils.nextInt(0, 1001));
        } catch (InterruptedException e) {
            // Nothing here
        }
    }

}
