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

using System;
using System.Threading;
using System.Threading.Tasks;
using Ice;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Parking.ZeroIce.Services;

namespace Parking.ZeroIce
{
    /// <summary>
    /// The Backend Service based in ZeroIce.
    /// </summary>
    internal class ParkingService : IHostedService, IDisposable
    {
        /// <summary>
        /// The Port.
        /// </summary>
        private const int Port = 8080;

        /// <summary>
        /// The Communicator.
        /// </summary>
        private readonly Communicator _communicator;

        /// <summary>
        /// The Logger.
        /// </summary>
        private readonly ILogger<ParkingService> _logger;

        /// <summary>
        /// The System.
        /// </summary>
        private readonly RepositoryDisp_ _theRepo;

        /// <summary>
        /// The Constructor.
        /// </summary>
        /// <param name="logger">to use.</param>
        /// <param name="theRepo">Connection to the Repository</param>
        public ParkingService(ILogger<ParkingService> logger, RepositoryDisp_ theRepo)
        {
            _logger = logger;
            _logger.LogDebug("Building ParkingService ..");
            _theRepo = theRepo;
            _communicator = BuildCommunicator();
        }

        /// <summary>
        /// Start the service.
        /// </summary>
        /// <param name="cancellationToken"></param>
        /// <returns></returns>
        public Task StartAsync(CancellationToken cancellationToken)
        {
            _logger.LogDebug("Starting the FivetService ..");

            // The adapter: https://doc.zeroc.com/ice/3.7/client-side-features/proxies/proxy-and-endpoint-syntax
            // tpc (protocol) -z (compression) -t 15000 (timeout in ms) -p 8080 (port to bind)
            var adapter = _communicator.createObjectAdapterWithEndpoints("Parking", "tcp -z -t 15000 -p " + Port);

            // Register in the communicator
            adapter.add(_theRepo, Util.stringToIdentity("Repository"));

            // Activation
            adapter.activate();

            // All ok
            return Task.CompletedTask;
        }

        /// <summary>
        /// Stop the service.
        /// </summary>
        /// <param name="cancellationToken"></param>
        /// <returns></returns>
        public Task StopAsync(CancellationToken cancellationToken)
        {
            _logger.LogInformation("Stopping the FivetService ..");

            _communicator.shutdown();

            _logger.LogDebug("Communicator Stopped!");

            return Task.CompletedTask;
        }

        /// <summary>
        /// Clean the house.
        /// </summary>
        public void Dispose()
        {
            _communicator.destroy();
        }

        /// <summary>
        /// Build the Communicator.
        /// </summary>
        /// <returns>The Communicator</returns>
        private Communicator BuildCommunicator()
        {
            _logger.LogDebug("Initializing Communicator v{0} ({1}) ..", Util.stringVersion(),
                Util.intVersion());

            // ZeroC properties
            Properties properties = Util.createProperties();
            // https://doc.zeroc.com/ice/latest/property-reference/ice-trace
            properties.setProperty("Ice.Trace.Admin.Properties", "1");
            properties.setProperty("Ice.Trace.Locator", "2");
            properties.setProperty("Ice.Trace.Network", "3");
            properties.setProperty("Ice.Trace.Protocol", "1");
            properties.setProperty("Ice.Trace.Slicing", "1");
            properties.setProperty("Ice.Trace.ThreadPool", "1");
            properties.setProperty("Ice.Compression.Level", "9");

            var initializationData = new InitializationData {properties = properties};

            return Util.initialize(initializationData);
        }
    }
}
