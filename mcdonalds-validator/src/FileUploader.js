import React, { useState } from 'react';
import './FileUploader.css';

function FileUploader() {
    const [selectedFile, setSelectedFile] = useState(null);
    const [response, setResponse] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleFileChange = (event) => {
        const file = event.target.files[0];
        if (file) {
            const validTypes = ['image/jpeg', 'image/png', 'image/jpg'];
            if (!validTypes.includes(file.type)) {
                setError('Invalid file type. Please upload a JPEG or PNG image.');
                setSelectedFile(null);
                return;
            }
            setSelectedFile(file);
            setError('');
        }
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        if (!selectedFile) {
            setError('Please select a file before submitting.');
            return;
        }

        const formData = new FormData();
        formData.append('file', selectedFile);
        setLoading(true);
        setResponse('');
        setError('');

        try {
            const res = await fetch('http://localhost:5000/api/extract-validation-code', {
                method: 'POST',
                body: formData,
            });
            if (!res.ok) {
                throw new Error('Server error. Please try again later.');
            }
            const data = await res.json();
            setResponse(data.validationCode || 'Error extracting code');
        } catch (error) {
            setError(error.message || 'An error occurred during file upload.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="file-uploader-container">
            <h2>McDonald's Receipt Validator</h2>
            <form className="file-upload-form" onSubmit={handleSubmit}>
                <input
                    type="file"
                    className="file-input"
                    onChange={handleFileChange}
                    accept=".jpeg,.png,.jpg"
                />
                <button type="submit" className="submit-button">Upload</button>
            </form>
            {loading && <p className="loading-message">Processing...</p>}
            {response && <p className="response-message">Validation Code: {response}</p>}
            {error && <p className="error-message">{error}</p>}
        </div>
    );
}

export default FileUploader;
