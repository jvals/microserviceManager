# Microservice Manager

## Quickstart
`./gradlew installDist` will build the application. 

`./msm` will run the application.

`./msm docker-compose` will generate a docker-compose.yml from the configuration
files in the _config_ directory.

## Motivation
Working with microservices can be challenging if you have to change your active
environment multiple times per day. 

I often find myself starting the day with one application, let's call it
application A. Then I need to test my changes with another application,
application B. So I power down application A, change the configuration of A to
point to look for my local version of B, and vice versa. Then I power both up
and verify that everything works as expected.

When the task is finished, I might go to start up application C, D, E, where C
and D are connected to E, and E is connected to A. B is not needed now, so we
power down B, change configurations in the other applications and power them up.

This gets tiring very quickly. Working like this requires knowledge of multiple
configuration formats, what needs to be connected where, which ports things are
running on, etc. The number of applications you have to juggle seem to explode
once you start working with micro-frontends, which (in my experience) are much
smaller and more numerous than traditional "backend" microservices.

If you always spin up the same applications, you can create a small set of
scripts that spin up the environments that you need. However, if your
environment is dynamic, the number of such configurations would grow
exponentially. In an environment where any app can be either on or off, you have 
2^n possible configurations, where n is the number of applications.  
 
## About
msm allows you to generate configurations in a runnable format, with minimal
input required. Any known service can be toggled on and off, and the application
will handle the configuration and the wiring. The available formats are described below.

## Formats
### Docker Compose
```
./msm docker-compose --help
Usage: app docker-compose [OPTIONS]

Options:
  -c, --connection-config TEXT     Path to connection configuration (default:
                                   config/connectionConfig.yml)
  -d, --docker-compose-blueprint TEXT
                                   Path to docker compose input (default:
                                   config/dockerComposeBlueprint.yml)
  -o, --output TEXT                Write to file instead of stdout
  -r, --run-config TEXT            Path to run configuration (default:
                                   config/runConfig.yml)
  -h, --help                       Show this message and exit
```

Given three inputs, this command will generate a docker-compose.yml. The
application will look for inputs in the config folder, but you can specify the
location of each input using command line flags.

The first input is _dockerComposeBlueprint.yml_. This file should be a valid docker
compose file, and it should contain all known services, volumes, networks and
everything else a docker-compose.yml can contain.

The second input is _connectionConfig.yml_. This file should describe the
relationship between all known services. Note that "connections" are implemented
as environment variables. Therefore, your service must support configuration
with environment variables.

The third input is _runConfig.yml_. This file contains "modes" for your 
services, "NOOP", "RUN" and "BUILD". 
* A service in NOOP mode will _not_ be included in the output docker-compose.yml. 
* A service in RUN mode will be included with its image property set. This will 
result in a container created from a pre built image.
* A service in BUILD mode will be included and its image will be built locally.

This three-way split means most of your changes will be to the "modes" in the
runConfig, while the other input files (after some initial setup) will remain
untouched.

## Examples
You can find an example with instructions in the examples folder.  
See [examples/micro-frontend-hello-world](examples/micro-frontend-hello-world)
