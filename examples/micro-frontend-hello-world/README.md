The purpose of this example is to show how you can use _msm_ to generate
a runnable script with a few inputs.

This example contains three front-ends: title-podlet, button-podlet and hello-world-layout.
All three apps are built with Podium. You can read more about Podium here: https://podium-lib.io/.

# How to build and run
0. Open runConfig.yml and add the absolute path to the three apps in the "context" field.
0. Make sure your working directory is at the root of this project
1. Symlink or copy the three files in the config folder to the root config folder.  
`ln -sr ./examples/micro-frontend-hello-world/config/*.yml ./config/`
2. Generate a docker-compose.yml file  
`./msm docker-compose -o docker-compose.yml`
3. Run docker-compose (make sure that Docker is running in the background)  
`docker-compose up --build --remove-orphans --force-recreate`
4. Open http://localhost:8000 in your browser
5. Optional:  Make some changes to runConfig.yml and repeat steps 3. 4. and 5.
