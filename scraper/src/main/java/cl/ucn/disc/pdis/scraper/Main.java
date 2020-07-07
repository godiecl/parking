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
    public static void main(String[] args) throws IOException {

        // from .. to ..
        final int ini = 21;
        final int end = 29; // 40000;

        // Contenedor of Fichas
        final List<DirectorioUCN.Ficha> fichas = new ArrayList<>(100);

        log.debug("Getting the data from {} to {} ...", ini, end);
        for (int id = ini; id <= end; id++) {

            log.debug("Testing for id {} ...", id);

            // Get the fichas from the agenda ucn
            final DirectorioUCN.Ficha ficha = DirectorioUCN.scrape(id);
            if (ficha != null) {
                // Insert into the list
                fichas.add(ficha);
            }

            // Just wait
            sleep();

        }

        log.debug("Fichas founded: {}", fichas.size());

        log.debug("Saving in json ..");

        // Gson to use to convert List<Ficha> to JSON.
        final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        // Write in a file
        try (final Writer writer = Files.newBufferedWriter(Paths.get("fichas.json"))) {
            gson.toJson(fichas, writer);
        }

        log.debug("Done.");

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
