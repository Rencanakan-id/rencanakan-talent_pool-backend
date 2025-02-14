# Gunakan base image Python
FROM python:3.10

# Set direktori kerja di dalam container
WORKDIR /app

# Copy semua file proyek ke dalam container
COPY . .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Jalankan migrasi database & collectstatic
RUN python manage.py migrate
# RUN python manage.py collectstatic --noinput

# Jalankan server dengan Gunicorn
CMD ["gunicorn", "--bind", "0.0.0.0:8000", "rencanakan_talent_pool.wsgi:application"]
