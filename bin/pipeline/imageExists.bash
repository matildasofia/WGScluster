#!/bin/bash
image_name="$1"
images=$(docker images --format '{{.Repository}}')

if [[ $images == *$image_name* ]]; then
    echo "$image_name"
