package com.calvin.walletapi.infrastructure

import org.flywaydb.core.Flyway

object FlywayMigrations {
  def runMigrations(config: DatabaseConfig): Unit =
    Flyway
      .configure()
      .locations("classpath:migrations")
      .dataSource(config.url, config.user, config.password)
      .load()
      .migrate()
}
