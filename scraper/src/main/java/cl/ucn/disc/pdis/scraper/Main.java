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

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The Class.
 *
 * @author Diego Urrutia-Astorga.
 */
public final class Main {

    /**
     * The Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * The entry point.
     *
     * @param args to use.
     * @throws IOException if any error.
     */
    public static void main(String[] args) throws IOException {

        // from .. to ..
        final int ini = 1;
        final int end = 1; // 40000;

        log.debug("Getting the data from {} to {} ...", ini, end);
        for (int id = ini; id <= end; id++) {

            // Just wait
            try {
                Thread.sleep(1000 + RandomUtils.nextInt(0, 1001));
            } catch (InterruptedException e) {
                // Nothing here
            }

            Document document = Jsoup.connect(
                    "http://online.ucn.cl/directoriotelefonicoemail/fichaGenerica/?cod=" + id)
                    .get();

            log.debug("Title: {}", document.title());

            Element lblNombre = document.getElementById("lblNombre");
            if (lblNombre == null || StringUtils.isEmpty(lblNombre.text())) {
                log.warn("Can't find lblNombre in id {}, skipping ..", id);
                continue;
            }
            log.debug("lblNombre: [{}].", lblNombre.text());

            Element lblCargo = document.getElementById("lblCargo");
            log.debug("lblCargo: [{}].", lblCargo.text());

        }


    }

}
