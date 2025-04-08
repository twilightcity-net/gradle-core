# Contributing to TwilightCity Core Gradle Plugins

Thank you for your interest in contributing to the TwilightCity Core Gradle Plugins! This document provides guidelines and instructions for contributing to this project.

## Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md). Please be respectful and considerate of others. If you encounter any unacceptable behavior, please report it to conduct@twilightcity.net.

## Getting Started

1. **Fork the Repository**: Start by forking the repository to your GitHub account
2. **Clone Your Fork**: Clone your fork to your local machine
3. **Set Up Development Environment**:
   - Ensure you have Java 11 installed
   - Install Gradle 6.8.3
   - Set up your preferred IDE (IntelliJ IDEA or Eclipse recommended)

## Development Workflow

1. **Create a Branch**:
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/your-bug-fix
   ```

2. **Make Your Changes**:
   - Follow the existing code style and patterns
   - Write clear, concise commit messages
   - Include tests for new functionality
   - Update documentation as needed

3. **Run Tests**:
   ```bash
   gradle test
   gradle integrationTest
   ```

4. **Commit Your Changes**:
   ```bash
   git commit -m "Description of your changes"
   ```

5. **Push to Your Fork**:
   ```bash
   git push origin your-branch-name
   ```

6. **Create a Pull Request**:
   - Go to the original repository
   - Click "New Pull Request"
   - Select your branch
   - Fill in the PR template
   - Submit the PR

## Code Style Guidelines

- Follow the existing code style and patterns
- Use meaningful variable and method names
- Keep methods focused and single-purpose
- Add appropriate comments and documentation
- Follow Java best practices
- Use Lombok annotations where appropriate

## Testing Guidelines

- Write unit tests for all new functionality
- Include integration tests for complex features
- Ensure all tests pass before submitting a PR
- Follow the existing test patterns and structure
- Use Spock Framework for testing

## Documentation

- Update README.md for significant changes
- Add or update JavaDoc comments
- Document any new configuration options
- Include examples of usage where appropriate

## Pull Request Process

1. Ensure your PR addresses a single issue or feature
2. Include a clear description of the changes
3. Reference any related issues
4. Ensure all tests pass
5. Update documentation as needed
6. Be responsive to feedback and requested changes

## Review Process

- PRs will be reviewed by maintainers
- Feedback will be provided within a reasonable timeframe
- Be prepared to make changes based on feedback
- Maintainers may request additional tests or documentation

## Versioning

- Follow semantic versioning (MAJOR.MINOR.PATCH)
- Update version numbers in appropriate files
- Document breaking changes

## Release Process

1. Create a release branch
2. Update version numbers
3. Update CHANGELOG.md
4. Create a release tag
5. Submit PR for review
6. Merge after approval
7. Create GitHub release

## Questions?

If you have any questions about contributing, please:
- Open an issue in the repository
- Join our community discussions
- Contact the maintainers

Thank you for contributing to TwilightCity Core Gradle Plugins! 