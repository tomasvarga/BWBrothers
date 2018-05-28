# BWBrothers

BWBrothers stands for Brood War: Brothers and it was made for master thesis "Use of Machine learning techniques in Real-time strategy games" by Tomáš Varga at the Technical University of Košice.

It's an open-source project in which the server can predict player's strategy based on aggregating game actions by 240 frames which are coming from the StarCraft: Brood War. Four game strategies were annotated with binary target attribute.
For training purposes we used 216 bot vs. bot replays generated from SSCAIT.

Included strategies that can be predicted in this project:
1. CANNON RUSH
2. ZERGLING RUSH
3. 2-GATE
4. 3-GATE

Examples:
If a strategy is predicted "ACTIVE" is shown on the screen:

![CANNON RUSH](https://thumbs.gfycat.com/GoodDiligentCreature-max-1mb.gif)
<img src="https://s1.gifyu.com/images/zergling-rush.gif" width="280px" height="210px" />

**Note:** That many more can be added later via manual annotation (process will be described later).

## BWBigBrother
Is a Python server used primarily as a prediction machine.

## BWSmallBrother
Is a small Java client that can read actions from the game, and send it to the server.


![Architecture](https://github.com/tomasvarga/BWBrothers/blob/master/arch.jpg?raw=true)



**More information can be found inside: *UserManual.pdf*, *SystemManual.pdf*.**

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

* StarCraft: Brood War,
* BWAPI version: 4.1.2.

#### BWBigBrother
Python: 2.7+
  * Flask 0.10.1,
  * Numpy 1.10.4,
  * pandas 0.17.1,
  * scikit-learn 0.19.

#### BWSMallBrother
Java version: 1.8.0_144+ 32-bit,
  * bwmirror 2_5.jar – BWAPI Java interface,
  * ini4j.jar – easy work with the .ini files,
  * httpcore.jar and httpclient.jar – Apache library simplifies works with HTTP ,
  * gson.jar – Google library making it easier to wrap up JSON,
  * commons-logging.jar – Apache dependencies.


### Installing

A step by step series of examples that tell you how to get a development env running:

#### How to setup BWBigBrother

1. Located variable with IP address in Python script app.py, and change it to the IP address of your computer.
```
ip = "192.168.1.21"
```

2.  After that, launch the server with command from the terminal:
```
python app.py
```

3. Train the model:
The first step to train all the models is to visit web page stated in the terminal via your web browser.
In our example it would be at:
```
http:/192.168.1.21:8080/train
```

4. Test the server by visiting web page:
```
your-ip-address:your_port/test.
```

It should generate following JSON output:
```
{
  "cannon_rush": 1,
  "zergling_rush": 0,
  "two_gateways": 0,
  "three_gateways":0

}
```
If everything is working properly the sever should be ready.
Skip to the part -> *How to setup BWSmallBrother*

Optional step:
5. Removing all the models is possible by visiting this web page:  
```
your-ip:your_port/wipe
```
This will remove all the models and step 3. has to be repeated to create new models.

#### How to setup BWSmallBrother without IDE

1. Install [BWAPI-4.1.2](https://github.com/bwapi/bwapi/releases/download/v4.1.2/BWAPI_412_Setup.exe)
2. Copy *settings.ini* to C:\Users\YourProfileName for example: C:\Users\Tomas\
3. Change in the *settings.ini*: 
```
address = your_ip
port = your_port
name = your_starcraft_profile_name
```

4. Use cmd/terminal and go to the folder with BWSmallBrother-v1.jar, and launch it with command:
```
java -jar BWSmallBrother-v1.jar
```

5. Launch the game with ChaosLauncher

**Note:** How to setup BWSmallBrother with IDE (ECLIPSE) and without is also detailed in UserManual.


## FAQ
1. BWMirror API supports only x86 architecture:
You need to install JRE with 32-bit support.

## Author

* **Tomáš Varga**


## Paper
Work on this project was also published as a paper for DISA 2018.

## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgments

* [Department of Cybernetics and Artificial Intelligence](https://kkui.fei.tuke.sk/info/english)
* BWAPI community
* [SSCAIT - Student StarCraft AI Tournament & Ladder](https://sscaitournament.com)
* [Bot Juno](https://liquipedia.net/starcraft/Juno)
* [ScExtractor](https://github.com/phoglenix/ScExtractor) written by Glen Robertson
* [SKLearnFlask](https://github.com/amirziai/sklearnflask)

