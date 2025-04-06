FROM python:3.9-slim

WORKDIR /app

COPY test.py /app/

COPY requirements.txt /app/

# Copy images and videos directories
COPY images /app/images
COPY videos /app/videos

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

ENTRYPOINT ["python", "test.py"]