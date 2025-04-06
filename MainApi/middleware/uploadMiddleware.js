const multer = require('multer');
const path = require('path');

// Configure Multer storage
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, './public/uploads/');
    },
    filename: function (req, file, cb) {
        let ext = path.extname(file.originalname);

        // If no extension, determine it from mimetype
        if (!ext) {
            if (file.mimetype.startsWith('image/')) {
                ext = '.png';
            } else if (file.mimetype.startsWith('video/')) {
                ext = '.mp4';
            } else {
                ext = ''; // or a default like '.bin' if needed
            }
        }

        cb(null, Date.now() + ext);
    },
});


// File filter to allow only specific types
const fileFilter = (req, file, cb) => {
    cb(null,true);
    return;
    if (file.mimetype === 'image/jpeg' || file.mimetype === 'image/png') {
        cb(null, true);
    } else {
        cb(new Error('Invalid file type. Only JPEG and PNG are allowed.'), false);
    }
};

// Initialize Multer
const upload = multer({
    storage: storage,
    limits: { fileSize: 1024 * 1024 * 500 }, // 5 MB limit
    fileFilter: fileFilter,
});

// Export the middleware
module.exports = {
    upload,
};
