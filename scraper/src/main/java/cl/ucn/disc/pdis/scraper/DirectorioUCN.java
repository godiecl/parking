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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.helpers.MessageFormatter;

/**
 * The Class to scrape a {@link Persona}.
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
     * Recover all the data from Persona.
     *
     * @param id to scrape.
     * @return the {@link Persona}.
     */
    @SneakyThrows
    public static Persona scrape(Integer id) {

        // The URL to use
        final String url = MessageFormatter.format(URL, id).getMessage();
        log.debug("Using {} ..", url);

        // The document to use as source.
        final Document document = Jsoup.connect(url).get();

        // All the data ..
        final String nombre = getText(document, "lblNombre");
        if (StringUtils.isEmpty(nombre)) {
            log.debug("Name in id {} not found!", id);

            //noinspection ReturnOfNull
            return null;
        }

        // The data
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
                .direccionOficina(direccion)
                .build();

        return persona;
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
}
