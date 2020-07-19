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

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The Class.
 *
 * @author Diego Urrutia-Astorga.
 */
@Slf4j
public final class DatabaseUtils {

    /**
     * The key from the environment.
     */
    private static final String DB_KEY = System.getenv("DB_KEY");

    /**
     * @param filename to encrypt.
     */
    private static void encrypt(final String filename) {
        exec("PRAGMA rekey='" + DB_KEY + "'", filename + "?cipher=chacha20");
    }

    /**
     * @param filename to decrypt.
     */
    private static void decrypt(final String filename) {
        exec("PRAGMA rekey=''", filename + "?cipher=chacha20&key=" + DB_KEY);
    }

    /**
     * @param pragma to use.
     * @param file   to use.
     */
    private static void exec(final String pragma, final String file) {

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:file:" + file)) {

            try (Statement statement = conn.createStatement()) {

                boolean status = statement.execute(pragma);
                log.debug("Status: {}", status);
            }

        } catch (SQLException ex) {
            log.error("Error", ex);
        }
    }

    /**
     * The Main.
     *
     * @param args from console.
     */
    public static void main(final String[] args) {

        encrypt("personas.db");
        // decrypt("personas.db");

    }
}
