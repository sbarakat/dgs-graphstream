
## About

Script to create video animation of network partition assignments using GraphStream renderer.

Processes METIS network file and node assignments, converting into the DGS file format. Then uses
GraphStream to animate each frame, finally frames are stitched together into a video animation using ffmpeg.

This project was used to visualize the graph partitioning algorithm in [sbarakat/graph-partitioning](https://github.com/sbarakat/graph-partitioning).

Example:

![Animation](animation.gif?raw=true)

## Initial setup

```
virtualenv -p python3 env
pip3 install -r requirements.txt
```

## Generate Animation

Run the following commands:
```
source env/bin/activate
./genGraphStream.py inputs/network_1.txt inputs/assignments.txt output/ --dgs --frames --join --num-partitions 4
```

The output directory should now contain the following files:
* `*.dgs` - the files DGS files for each partition built by combining the METIS network file and the assignments.
* `frames_partition/` - individual frames for each step in the DGS file. Prefixed with the partition number, eg. `p1_*.png`
* `frames_joined/` - the frames from the folder above are joined to produce a single video frame. The video frame is stepped by node placement from the assignments file.

The video frames can be animated into an MP4 for playback using `ffmpeg`:

```
ffmpeg -framerate 4 -i output/frames_joined/frame_%6d.png -pix_fmt yuv420p -r 10 output/animation.mp4
ffmpeg -framerate 1 -i output/frames_joined/frame_%6d.png -pix_fmt yuv420p -r 10 output/animation_slow.mp4

# experiment with the options:
ffmpeg -r 5 -i output/frames_joined/frame_%6d.png -pix_fmt yuv420p -r 10 output/animation.mp4
# The first -r means the video will play at 1 of the original images per second.
# The second -r means the video will play at 10 frames per second.
```

## DGS to frames internals

The GraphStream renderer is already executed when generating the animation above. To generate the frames manually,
for example to experiment with the LinLog layout, the jar program can be executed directly.

```
$ java -jar "dgs-graphstream/dist/dgs-graphstream.jar" -h
Missing required option: -dgs

Missing required option: -out

usage: DgsGraphStreamAnimate.jar [OPTIONS]...
-dgs <arg>      input GraphStream DGS file
-out <arg>      frame filenames are prepended with this path
-layout <arg>   layout option to use. options: [default|linlog]
-display screen layout option to use. options: [screen]
-h,-help        display this help and exit
```

When used in this way, the `./genGraphStream.py` script can be used to create the DGS file, which is then fed into
the JAR to generate the frames and finally back into `./genGraphStream.py` to join them together. The commands
below give a full example for generating an animation using the the LinLog layout:

```
# Load the Python virtual environment
source env/bin/activate

# Generate DGS file from network and assignments
./genGraphStream.py inputs/network_1.txt inputs/assignments.txt output/ --dgs --num-partitions 4

# Animate the DGS file into frames for each partition
java -jar "dgs-graphstream/dist/dgs-graphstream.jar" -dgs output/partition_0.dgs -out output/frames_partition/p0_ -layout linlog
java -jar "dgs-graphstream/dist/dgs-graphstream.jar" -dgs output/partition_1.dgs -out output/frames_partition/p1_ -layout linlog
java -jar "dgs-graphstream/dist/dgs-graphstream.jar" -dgs output/partition_2.dgs -out output/frames_partition/p2_ -layout linlog
java -jar "dgs-graphstream/dist/dgs-graphstream.jar" -dgs output/partition_3.dgs -out output/frames_partition/p3_ -layout linlog

# Join each partition tile into a single frame
./genGraphStream.py inputs/network_1.txt inputs/assignments.txt output/ --join --num-partitions 4

# Convert the frames into a video
ffmpeg -framerate 4 -i output/frames_joined/frame_%6d.png -pix_fmt yuv420p -r 10 output/animation.mp4
```

## Author

Sami Barakat (<sami@sbarakat.co.uk>)

Licensed under the MIT license. See the [LICENSE](https://github.com/sbarakat/dgs-graphstream/blob/master/LICENSE) file for further details.
