FROM python:3.13-slim

WORKDIR /app

# Copy requirements and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the application
COPY diameter-server.py .

# Expose the diameter port
EXPOSE 3868

# Run the server
CMD ["python", "diameter-server.py"]
