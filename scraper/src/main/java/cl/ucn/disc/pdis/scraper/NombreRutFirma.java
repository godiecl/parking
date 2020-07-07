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

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class to scrape some data.
 *
 * @author Diego Urrutia-Astorga.
 */
@Slf4j
public final class NombreRutFirma {

    /**
     * The URL.
     */
    private static final String URL = "https://www.nombrerutyfirma.com/buscar";

    /**
     * Recover all the data for a name of {@link Persona}.
     *
     * @param term to search.
     * @return the {@link Persona}.
     */
    @SneakyThrows
    public static List<Rutificador> scrape(String term) {

        log.debug("Searching <{}> ..", term);

        // The list of data;
        final List<Rutificador> rutificadors = new ArrayList<>();

        final Document doc = Jsoup.connect(URL)
                // Search term
                .data("term", term)
                // Need this
                .referrer("https://www.nombrerutyfirma.com/")
                .post();

        final Element table = doc.select("table").get(0);
        // log.debug("The Table: {}", table);

        final Elements rows = table.select("tbody").select("tr");
        for (final Element row : rows) {

            Elements cols = row.select("td");
            // log.debug("Row: {}", cols);

            final String nombre = cols.get(0).text();
            final String rut = cols.get(1).text();
            final String sexo = cols.get(2).text();
            final String direccion = cols.get(3).text();
            final String comuna = cols.get(4).text();

            // The List of Rutificador
            final Rutificador rutificador = Rutificador.builder()
                    .nombre(nombre)
                    .rut(rut)
                    .sexo(sexo)
                    .direccion(direccion)
                    .comuna(comuna)
                    .build();

            rutificadors.add(rutificador);

        }

        return rutificadors;

    }

    /**
     * The Rutificador info.
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class Rutificador {

        @Getter
        private String nombre;

        @Getter
        private String rut;

        @Getter
        private String sexo;

        @Getter
        private String direccion;

        @Getter
        private String comuna;

    }

}
