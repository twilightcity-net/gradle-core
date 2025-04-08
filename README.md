# TwilightCity Core Gradle Plugins

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Gradle](https://img.shields.io/badge/Gradle-6.8.3-green.svg)](https://gradle.org)
[![Java](https://img.shields.io/badge/Java-11-blue.svg)](https://www.java.com)

A comprehensive collection of Gradle plugins designed to standardize and streamline project development across the TwilightCity ecosystem. These plugins provide a unified approach to building, testing, and managing Java-based projects.

## Features

- **Unified Testing Framework**: Centralized test harness that enables running tests across multiple modules with a single command
- **Standardized Project Configuration**: Consistent project setup and defaults across all modules
- **Enhanced IDE Support**: Improved integration with IntelliJ IDEA and Eclipse
- **License Management**: Automated license header management and verification
- **Build Optimization**: Performance optimizations and standardized build configurations
- **Docker Integration**: Simplified Docker container management for development and testing
- **Database Support**: Built-in support for MongoDB and PostgreSQL in development environments

## Available Plugins

- `net.twilightcity.core`: Core plugin providing base functionality
- `net.twilightcity.core-oss`: Open source specific configurations
- `net.twilightcity.java-ext`: Enhanced Java project support
- `net.twilightcity.ide-ext`: Advanced IDE integration
- `net.twilightcity.license-ext`: License management
- `net.twilightcity.test-ext`: Extended testing capabilities
- `net.twilightcity.project-defaults`: Standardized project defaults
- `net.twilightcity.project-support`: Additional project utilities
- `net.twilightcity.spring`: Spring Framework integration
- `net.twilightcity.mongo`: MongoDB development support
- `net.twilightcity.postgres`: PostgreSQL development support

## Requirements

- Java 11 or later
- Gradle 6.8.3 or later

## Installation

Add the following to your `build.gradle`:

```groovy
plugins {
    id 'net.twilightcity.core' version '2.0'
}
```

## Usage

### Basic Setup

Apply the core plugin to your project:

```groovy
apply plugin: 'net.twilightcity.core'
```

### Running Tests

To run all tests across all modules:

```bash
gradle test
```

### IDE Integration

For IntelliJ IDEA users:

```bash
gradle refreshIdea
```

For Eclipse users:

```bash
gradle refreshEclipse
```

### License Management

To check license headers:

```bash
gradle checkLicense
```

To format license headers:

```bash
gradle formatLicense
```

## Contributing

We welcome contributions! Please read our [Contributing Guidelines](CONTRIBUTING.md) for details on how to get started, our development workflow, and the process for submitting pull requests.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE.txt](LICENSE.txt) file for details.

## Support

For support, please open an issue in the GitHub repository.

## Acknowledgments

- [Gradle](https://gradle.org) - The build system
- [JGit](https://www.eclipse.org/jgit/) - Git integration
- [Lombok](https://projectlombok.org) - Java annotation processor
- [Spock Framework](https://spockframework.org) - Testing framework
