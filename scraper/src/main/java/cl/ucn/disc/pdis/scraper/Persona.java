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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * The Persona Class.
 *
 * @author Diego Urrutia-Astorga.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public final class Persona {

    /**
     * The id: Primary Key (autoincrement).
     */
    @Getter
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The Key of Directory UCN.
     */
    @Column(unique = true, nullable = false)
    private Integer codigo;

    /**
     * The Rut.
     */
    @Getter
    @Setter
    @Column(unique = true)
    private String rut;

    /**
     * The Nombre.
     */
    @Getter
    @Column
    private String nombre;

    /**
     * The Email.
     */
    @Getter
    @Column
    private String email;

    /**
     * The Cargo.
     */
    @Getter
    @Column
    private String cargo;

    /**
     * The Unidad.
     */
    @Getter
    @Column
    private String unidad;

    /**
     * The Oficina.
     */
    @Getter
    @Column
    private String oficina;

    /**
     * The Direccion.
     */
    @Getter
    @Column
    private String direccionOficina;

    /**
     * The Sexo.
     */
    @Getter
    @Setter
    @Column
    private Sexo sexo;

    /**
     * The Direccion.
     */
    @Getter
    @Setter
    @Column
    private String direccion;

    /**
     * The Direccion.
     */
    @Getter
    @Setter
    @Column
    private String comuna;

    /**
     * The telefono fijo.
     */
    @Getter
    @Column
    private String telefonoFijo;

    /**
     * The telefono movil.
     */
    @Getter
    @Column
    private String telefonoMovil;

    /**
     * The Status.
     */
    @Getter
    @Setter
    @Column
    private Status status;

    /**
     * The Sexo.
     */
    public enum Sexo {
        /**
         * The Masculino.
         */
        MASCULINO,
        /**
         * The Femenino.
         */
        FEMENINO,
    }

    /**
     * The Status.
     */
    public enum Status {
        /**
         * Not found in Directorio UCN.
         */
        UCN_NOTFOUND,
        /**
         * Getted from Directorio UCN.
         */
        UCN_SCRAPED,
        /**
         * Not found in Rutificador.
         */
        NRF_NOTFOUND,
        /**
         * Found 1 in Rutificador.
         */
        NRF_SCRAPED,
        /**
         * Found more than 1 in Rutificador.
         */
        NRF_MANY,
        /**
         * Sended over ZeroIce.
         */
        UPLOADED,
    }

}
