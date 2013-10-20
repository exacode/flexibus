Flexible EventBus
=================

This EventBus is based on event bus from (guava)[http://code.google.com/p/guava-libraries/wiki/EventBusExplained]. Hopefully it doesn't break the license (otherwise please contact).

Modifications
-------------
Provided modifications opens this EventBus for (magic things)[https://code.google.com/p/guava-libraries/wiki/EventBusExplained#Why_can't_I_do_<magic_thing>_with_EventBus_?] like:
- changing event handler annotation
- providing strategy for event dispatching `DispatchStrategy`
- providing startegy for finding handler methods `MethodHandlerFinder`
- providing exception handler `ExceptionHandler`
- using parameters of primitive types in handler methods
- ...and it doesn't depend on guava library (Guava is a fairly sizable JAR file)

Eventbus for spring!
--------------------
Along with [flexible-eventbus](/flexible-eventbus) there is also [spring-flexible-eventbus](/spring-flexible-eventbus) project that inegrates eventbus with spring environemnt. Try it out or take a look at how simple it is: 
- (TestConfiguration)[/spring-flexible-eventbus/src/main/test/net/exacode/eventbus/spring/TestConfiguration.java]
- (Simple usage)[/spring-flexible-eventbus/src/main/test/net/exacode/eventbus/spring/SpringEventBusPostProcessorTest.java]

Maven dependency
----------------
In order to use this library add [repository](http://github.com/exacode/mvn-repo) location into your `pom.xml` 
and add appropriate dependency.

		<dependency>
			<groupId>net.exacode.eventbus</groupId>
			<artifactId>flexible-eventbus</artifactId>
			<version>${project.version}</version>
		</dependency>

<a href='http://www.pledgie.com/campaigns/22342'><img alt='Click here to lend your support to: Exacode open projects and make a donation at www.pledgie.com !' src='http://www.pledgie.com/campaigns/22342.png?skin_name=chrome' border='0' /></a>
