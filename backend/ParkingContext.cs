using System.Reflection;
using Microsoft.EntityFrameworkCore;
using Parking.ZeroIce.Model;

namespace Parking.Dao
{
    /// <summary>
    /// Connection to the Database.
    /// </summary>
    public class ParkingContext : DbContext
    {
        /// <summary>
        /// Table: Personas
        /// </summary>
        public DbSet<Persona> Personas {
            get;
            set;
        }

        /// <summary>
        /// Configuration.
        /// </summary>
        /// <param name="optionsBuilder"></param>
        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            // Using SQLite
            optionsBuilder.UseSqlite("Data Source=parking.db", options =>
            {
                options.MigrationsAssembly(Assembly.GetExecutingAssembly().FullName);
            });
            base.OnConfiguring(optionsBuilder);
        }

        /// <summary>
        /// Create the ER from Entity.
        /// </summary>
        /// <param name="modelBuilder">to use</param>
        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // Update the model
            modelBuilder.Entity<Persona>(builder =>
            {
                // Primary Key
                builder.HasKey(p => p.uid);
                // Required rut
                // builder.Property(p => p.rut).IsRequired();
                // builder.HasIndex(p => p.rut).IsUnique();
                // Email is required
                // builder.Property(p => p.email).IsRequired();
                // Email is unique!
                // builder.HasIndex(p => p.email).IsUnique();
            });

        }

    }
}
