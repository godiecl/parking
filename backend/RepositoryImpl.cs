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

using Ice;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Parking.Dao;
using Parking.ZeroIce.Model;

namespace Parking.ZeroIce.Services
{
    /// <summary>
    /// Repository (ZeroIce) implementation.
    /// </summary>
    public class RepositoryImpl : RepositoryDisp_
    {
        /// <summary>
        /// The Logger.
        /// </summary>
        private readonly ILogger<RepositoryImpl> _logger;

        /// <summary>
        /// The Provider of DbContext.
        /// </summary>
        private readonly IServiceScopeFactory _serviceScopeFactory;

        /// <summary>
        ///
        /// </summary>
        /// <param name="logger"></param>
        /// <param name="serviceScopeFactory"></param>
        public RepositoryImpl(ILogger<RepositoryImpl> logger, IServiceScopeFactory serviceScopeFactory)
        {
            _logger = logger;
            _logger.LogDebug("Building the ReposityImpl ..");
            _serviceScopeFactory = serviceScopeFactory;

            // Create the database
            _logger.LogInformation("Creating the Database ..");
            using (var scope = _serviceScopeFactory.CreateScope())
            {
                var fc = scope.ServiceProvider.GetService<ParkingContext>();
                fc.Database.EnsureCreated();
                fc.SaveChanges();
            }

            _logger.LogDebug("Done.");
        }

        /// <summary>
        /// Save a Persona into the Database.
        /// </summary>
        /// <param name="persona">toSave.</param>
        /// <param name="current">Connection to the communicator</param>
        /// <returns></returns>
        public override Persona save(Persona persona, Current current = null)
        {
            _logger.LogDebug("Saving Persona {}", persona);

            // Using the local scope
            using (var scope = _serviceScopeFactory.CreateScope())
            {
                var pc = scope.ServiceProvider.GetService<ParkingContext>();
                pc.Personas.Add(persona);
                pc.SaveChanges();
                return persona;
            }
        }
    }
}
