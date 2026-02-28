# Contributing to Habit Tracker

Thank you for your interest in contributing! This document provides guidelines and instructions for contributing.

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on the code, not the person
- Help others learn and grow

## How to Contribute

### 1. **Report Bugs**

Create an issue with:
- Clear title and description
- Steps to reproduce
- Expected vs actual behavior
- Android version, device model
- Logcat output (if applicable)

### 2. **Suggest Features**

- Describe the feature and its use case
- Explain why it would be valuable
- Provide examples of similar features
- Link to any relevant issues/discussions

### 3. **Submit Code Changes**

#### Prerequisites
- JDK 17 or higher
- Android Studio (latest)
- Kotlin knowledge
- Familiarity with Clean Architecture

#### Process

1. **Fork the repository**
   ```bash
   git clone https://github.com/[your-username]/HabitTracker.git
   cd HabitTracker
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/your-bug-fix
   ```

3. **Make your changes**
   - Follow Kotlin Official Style Guide
   - Write clear, descriptive commit messages
   - Add unit tests for new functionality
   - Update README if needed

4. **Run checks**
   ```bash
   # Format code
   ./gradlew ktlintFormat

   # Run analysis
   ./gradlew detekt

   # Run tests
   ./gradlew testDebugUnitTest

   # Check coverage (target: 80%+)
   ./gradlew jacocoTestReport
   ```

5. **Commit and push**
   ```bash
   git add .
   git commit -m "feat: Add new feature description"
   git push origin feature/your-feature-name
   ```

6. **Create a Pull Request**
   - Reference any related issues (#123)
   - Provide clear description of changes
   - Ensure all CI checks pass
   - Request review from maintainers

## Code Style Guide

### Kotlin Conventions
- **Naming**: camelCase for variables, PascalCase for classes
- **Formatting**: 4-space indentation, 120 char line limit
- **Ordering**: Public → Protected → Private
- **Documentation**: KDoc for public APIs

### Architecture
- **Layer Separation**: Don't import from Presentation to Repository
- **Dependency Injection**: Use Hilt, no manual singleton creation
- **Flow/Coroutines**: Suspend functions for DB/Network operations
- **Error Handling**: Use Result<T> and UiState<T> wrappers

### Composable Guidelines
- Keep composables small and focused
- Use meaningful parameter names
- Add `@Preview` for testable composables
- Ensure 48dp minimum touch target size

## Testing Requirements

- **Unit Tests**: Use MockK for mocks, Turbine for Flow testing
- **Integration Tests**: Use in-memory Room database
- **UI Tests**: Compose UI Test for critical flows
- **Coverage**: Minimum 80% for modified code

Example:
```kotlin
@Test
fun `should create habit successfully`() = runTest {
    val habit = Habit(name = "Exercise", frequency = HabitFrequency.DAILY)
    val result = createHabitUseCase(habit)

    assertTrue(result is Result.Success)
}
```

## Commit Message Format

Follow conventional commits:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

**Example**:
```
feat(habits): add habit reordering with drag-and-drop

- Implement reorder use case
- Add drag-and-drop UI in HabitsScreen
- Persist order to database

Closes #42
```

## Review Process

1. Automated checks must pass (CI/CD pipeline)
2. Code review by maintainers
3. Approval required before merge
4. Squash commits on merge

## Areas We Need Help With

- [ ] Complete CreateHabitScreen UI
- [ ] HabitDetailScreen implementation
- [ ] SettingsScreen with permissions
- [ ] Bottom navigation integration
- [ ] Habit completion logging flow
- [ ] Animations and transitions
- [ ] Accessibility improvements
- [ ] Documentation and KDoc
- [ ] More unit tests
- [ ] Performance optimization

## Getting Help

- Check [issues](https://github.com/[username]/HabitTracker/issues) for discussions
- Open a discussion for questions
- Review [README.md](README.md) for architecture overview
- Check inline code comments for implementation details

## License

By contributing, you agree your code is licensed under MIT License.

---

**Thank you for contributing to Habit Tracker! 🎉**
