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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * The Class.
 *
 * @author Diego Urrutia-Astorga.
 */
@Slf4j
public final class ScrapperUtils {

    /**
     * Time between retries
     */
    private static final int WAIT = 5;

    /**
     * Time to wait for the request
     */
    private static final int TIMEOUT = 10;

    /**
     * Number of retries.
     */
    private static final int RETRIES = 3;

    /**
     * Method to retry 3 times a failed request.
     * @param url used to get the {@link Document}.
     * @return the {@link Document} from the url.
     */
    @SneakyThrows
    static Document retrieve(String url) {


        // Cicle to try to get the document
        for (int i = 1; i <= RETRIES; i++) {

            try {
                return Jsoup.connect(url)
                    .timeout(10 * 1000)
                    .userAgent("Mozilla\\/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit\\/537.36 (KHTML, like Gecko) Chrome\\/85.0.4171.0 Safari\\/537.36")
                    .get();

            } catch (UnknownHostException | SocketTimeoutException ex) {
                log.warn("Try {} error !!", i, ex);

                // Max retries, send the error
                if (i == RETRIES) {
                    throw  ex;
                }
                // Wait 5 seconds
                Thread.sleep(5 * 1000);
            }

        }
        //noinspection ReturnOfNull
        return null;
    }
}
