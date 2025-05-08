
# dro1d-libs-java

A collection of reusable Java libraries designed to simplify application development and improve productivity. These libraries provide utilities for command line (CLI) arguments, I/O operations, localization, and more.

## Features

- **Command Management**: Simplify the creation and execution of CLI arguments.
- **I/O Utilities**: Streamline input/output operations with easily reusable implementations.
- **Localization**: Manage, create, and use locales with ease.
- **Extensibility**: Designed to be modular and easy to integrate into your projects.

## Getting Started

### Prerequisites

- Java 21 or higher
- LocaleManager requires org.json library to read & write locale files.
- Swing GUI package uses flatlaf library (min: 3.4). Some things will definitely break without it, but components and the like should still work. It will be hit & miss, try it if you like.

### Installation

Add the JAR file as a dependency in your project. If you use LocaleManager, you will also need to add the org.json library, and if you use the Swing GUI stuff, you should add the flatlaf library (hit & miss regarding what breaks if you don't include it).

I haven't made a release jar yet, but you can clone the repository and build it from source. To build successfully, you will need the pre-requisites mentioned above, and a Java 21+ JDK.

## Usage

Please refer to the [javadocs](https://everdro1d.github.io/dro1d-libs-java/) for detailed documentation on each class and its methods.

### Example: Command Management
```java
CommandManager commandManager = new CommandManager();
commandManager.registerCommand("example", new ExampleCommand());
commandManager.executeCommand("example");
```

### Example: Localization
```java
LocaleManager localeManager = new LocaleManager(Main.class, "dro1dDev");
localeManager.loadLocaleFromFile("eng");
String message = localeManager.getVariableInComponent("MainWindow", "greetingLabel", "messageText");
System.out.println(message);
```

## Contributing

Contributions are welcome. Please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bugfix.
3. Make your changes and create documentation (javadoc comments).
4. Create and run tests to ensure everything works as expected.
5. Submit a pull request with a clear description of your changes.

## Support

For issues or questions, please [open an issue](https://github.com/everdro1d/dro1d-libs-java/issues/new/choose) in the repository or [contact me](mailto:everdro1d-github@pm.me).

