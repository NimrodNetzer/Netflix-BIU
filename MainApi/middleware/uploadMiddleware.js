const multer = require('multer');
const { CloudinaryStorage } = require('multer-storage-cloudinary');
const cloudinary = require('cloudinary').v2;

cloudinary.config({
    cloud_name: process.env.CLOUDINARY_CLOUD_NAME,
    api_key: process.env.CLOUDINARY_API_KEY,
    api_secret: process.env.CLOUDINARY_API_SECRET,
});

const storage = new CloudinaryStorage({
    cloudinary,
    params: (_req, file) => {
        const isVideo = file.mimetype.startsWith('video/');
        return {
            folder: isVideo ? 'netflix/videos' : 'netflix/images',
            resource_type: 'auto',
        };
    },
});

const upload = multer({
    storage,
    limits: { fileSize: 1024 * 1024 * 500 }, // 500 MB
});

module.exports = { upload };
