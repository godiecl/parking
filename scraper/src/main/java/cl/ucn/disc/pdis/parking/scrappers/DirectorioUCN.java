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

package cl.ucn.disc.pdis.parking.scrappers;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.helpers.MessageFormatter;

/**
 * The Class to scrape a {@link Ficha}.
 *
 * @author Diego Urrutia-Astorga.
 */
@Slf4j
public final class DirectorioUCN {

    /**
     * The URL.
     */
    private static final String URL = "http://online.ucn.cl/directoriotelefonicoemail/fichaGenerica/?cod={}";

    /**
     * Recover all the data.
     *
     * @param id to scrape.
     * @return the {@link Ficha}.
     */
    @SneakyThrows
    public static Ficha scrape(Integer id) {

        // The URL to use
        final String url = MessageFormatter.format(URL, id).getMessage();
        log.debug("Using {} ..", url);

        // The document to use as source.
        final Document document = ScrapperUtils.retrieve(url);

        // All the data ..
        final String nombre = getText(document, "lblNombre");
        if (StringUtils.isEmpty(nombre)) {
            log.debug("Ficha id {} not found!", id);

            //noinspection ReturnOfNull
            return null;
        }

        // The data
        final String cargo = getText(document, "lblCargo");
        final String unidad = getText(document, "lblUnidad");
        final String email = getText(document, "lblEmail");
        final String telefono = StringUtils.remove(getText(document, "lblTelefono"), "Fono ");
        final String oficina = getText(document, "lblOficina");
        final String direccion = getText(document, "lblDireccion");


        // Create a new Ficha.
        return Ficha.builder()
                .nombre(nombre)
                .cargo(cargo)
                .unidad(unidad)
                .email(email)
                .telefono(telefono)
                .direccion(direccion)
                .build();

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
     * The Funcionario/Academico info.
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class Ficha {

        @Getter
        private String nombre;

        @Getter
        private String cargo;

        @Getter
        private String unidad;

        @Getter
        private String email;

        @Getter
        private String telefono;

        @Getter
        private String oficina;

        @Getter
        private String direccion;

    }

}
