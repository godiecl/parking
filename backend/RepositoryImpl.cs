using Ice;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Parking.Dao;
using Parking.ZeroIce.Model;
using Parking.ZeroIce.Services;

namespace Parking.ZeroIce
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
