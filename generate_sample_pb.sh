#!/bin/bash

# Script to generate sample_email.pb file
# This script runs the test that generates the sample file

echo "Generating sample_email.pb file..."
echo ""

# Run the test that generates the file
./gradlew testDebugUnitTest --tests "*GenerateSamplePbFileTest*" --quiet

# Check if file was created
if [ -f "sample_email.pb" ]; then
    echo "Success: sample_email.pb created in project root"
    echo "File size: $(ls -lh sample_email.pb | awk '{print $5}')"
    echo "Location: $(pwd)/sample_email.pb"
else
    echo "File not found in project root. Checking other locations..."
    find . -name "sample_email.pb" -type f 2>/dev/null | head -3
fi

