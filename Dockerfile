# start with opensurf java
FROM stonerworx/opensurf-java

# copy open cv nonfree to java.library.path
RUN cp /home/opencv/build/lib/libopencv_nonfree.so /usr/lib

# create the application directory and copy the local files there
RUN mkdir -p /home/featureutils
WORKDIR /home/featureutils
COPY ./ /home/featureutils/

RUN gradle build

#copy java library
RUN cp /home/featureutils/build/libs/featureutils-1.0.jar /home/javalibs/featureutils-1.0.jar