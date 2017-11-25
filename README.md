
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

## Author

Sami Barakat (<sami@sbarakat.co.uk>)

Licensed under the MIT license. See the [LICENSE](https://github.com/sbarakat/dgs-graphstream/blob/master/LICENSE) file for further details.
