Flexibus - Flexible EventBus
============================

[![Flattr this!](https://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=exacode&url=https://github.com/exacode/flexibus&tags=eventbus,java,code,github&category=software) 
[![Build Status](https://travis-ci.org/exacode/flexibus.png?branch=master)](https://travis-ci.org/exacode/flexibus)

This EventBus is based on [Guava](http://code.google.com/p/guava-libraries/wiki/EventBusExplained). Hopefully it doesn't break the license (otherwise please contact me).

Modifications
-------------
Provided modifications opens this EventBus for [magic things](https://code.google.com/p/guava-libraries/wiki/EventBusExplained#Why_can't_I_do_<magic_thing>_with_EventBus_?) like:
- changing event handler annotation
- providing strategy for event dispatching - `DispatchStrategy`
- providing strategy for finding handler methods - `MethodHandlerFinder`
- providing exception handler - `ExceptionHandler`
- using parameters of primitive types in handler methods
- ...and it doesn't depend on guava library (Guava is a sizeable jar file)

__Example configuration of EventBus__

		EventBus defaultBus = new EventBus(); // Uses default settings: exception logging, dead event logging, searches for methods annotated with @EventHandler

		EventBus simpleBus = EventBus.builder()
			.withLoggingExceptionHandler() // Logs exceptions
			.withDeadEventLogHandler()     // Logs dead events
			.withAsyncDispatchStrategy()   // Dispatches events in asynchronous way (you can change it to SyncDispatchStrategy)
			.annotatedMethodHandlerFindingStrategy(YourAnnotation.class) // Searches for methods annotated with @YourAnnotation
			.buildEventBus();

		EventBus advancedBus = EventBus.builder()
			.exceptionHandler(yourExceptionHandler)
			.eventDispatchStrategy(yourEventDispatchStrategy)
			.methodHandlerFindingStrategy(yourMethodHandlerFindingStrategy)
			.buildEventBus();


Eventbus for Spring Framework!
------------------------------
Along with [flexibus](/flexibus) there is also [flexibus-spring](/flexibus-spring) project that integrates eventbus with spring environment. Try it out or take a look at examples: 
- [TestConfiguration](/flexibus-spring/src/test/java/net/exacode/eventbus/spring/TestConfiguration.java)
- [Simple usage](/flexibus-spring/src/test/java/net/exacode/eventbus/spring/SpringEventBusPostProcessorTest.java)

__Configuration__

		@Configuration
		@ComponentScan(basePackageClasses = TestConfiguration.class)
		public class TestConfiguration {

			@Bean
			public EventBus eventBus() {
				return new EventBus();
			}

			@Bean
			public SpringEventBusPostProcessor springEventBusPostProcessor() {
				return new SpringEventBusPostProcessor(eventBus());
			}

		}

__Event emitter__

		@Component
		public class EventEmitter {

			@Autowired
			private EventBus eventBus;


			@Test
			public void shouldDeliverEvent() {
				eventBus.post("Hello");
			}

		}

__Event receiver__

		@Component
		public class EventReceiver {


			@EventHandler
			public void handle(String event) {
				// ...
			}

		}

Maven dependency
----------------
In order to use this library add [repository](http://github.com/exacode/mvn-repo) location into your `pom.xml` 
and add appropriate dependency.

		<dependency>
			<groupId>net.exacode.eventbus</groupId>
			<artifactId>flexibus</artifactId>
			<version>${project.version}</version>
		</dependency>
