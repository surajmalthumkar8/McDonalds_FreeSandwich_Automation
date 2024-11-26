const express = require('express');
const multer = require('multer');
const cors = require('cors');
const path = require('path');
const { exec } = require('child_process');

const app = express();
app.use(cors());

// Configure multer for file uploads (store files in 'uploads' directory)
const upload = multer({ dest: 'uploads/' });

// Function to run the Java program and extract the validation code
const runJavaAutomation = (filePath) => {
    return new Promise((resolve, reject) => {
        const javaFilePath = 'C:\\Users\\Suraj\\McDonalds_Script\\Maven_McDonalds\\src\\main\\java';
        const command = `java -cp "${javaFilePath}" org.example.McDonaldsSurveyAutomation "${filePath}"`;

        exec(command, (error, stdout, stderr) => {
            if (error) {
                reject(`Error: ${stderr}`);
            } else {
                resolve(stdout);
            }
        });
    });
};

// Route to handle file uploads
app.post('/api/extract-validation-code', upload.single('file'), async function (req, res, next) {
    try {
        if (!req.file) {
            console.error('No file received.');
            return res.status(400).json({ error: 'No file uploaded' });
        }

        console.log('File received:', req.file);

        const filePath = path.resolve(req.file.path);
        console.log('Resolved file path:', filePath);

        const validationCode = await runJavaAutomation(filePath);
        res.json({ validationCode: validationCode.trim() });
    } catch (error) {
        console.error('Error extracting validation code:', error);
        res.status(500).json({ error: 'Failed to extract validation code' });
    }
});

// Start the server
const PORT = 3005;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
