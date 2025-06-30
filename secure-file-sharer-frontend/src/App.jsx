/* global QRious */ // ESLint directive to recognize QRious as a global variable
import React, { useState, useEffect, useRef } from 'react';

// Include Tailwind CSS (assuming it's loaded via CDN in the parent environment)
// You would typically link it in your public/index.html or use a build process.
// <script src="https://cdn.tailwindcss.com"></script>
// Configure Tailwind to include Inter font and extend colors if needed
// <script>
//   tailwind.config = {
//     theme: {
//       extend: {
//         fontFamily: {
//           sans: ['Inter', 'sans-serif'],
//         },
//         colors: {
//           'primary-blue': '#4A90E2',
//           'secondary-gray': '#F2F5F8',
//           'text-gray': '#6B7280',
//           'accent-green': '#10B981',
//           'error-red': '#EF4444',
//         }
//       }
//     }
//   }
// </script>

// Lucide React icons (simulated, in a real React app you'd import from 'lucide-react')
const UploadIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-upload"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" x2="12" y1="3" y2="15"/></svg>
);
const DownloadIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-download"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" x2="12" y1="15" y2="3"/></svg>
);
const ShareIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-share2"><circle cx="18" cy="5" r="3"/><circle cx="6" cy="12" r="3"/><circle cx="18" cy="19" r="3"/><line x1="8.59" x2="15.42" y1="13.51" y2="17.49"/><line x1="15.41" x2="8.59" y1="6.51" y2="10.49"/></svg>
);
const FileIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-file"><path d="M15 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7Z"/><path d="M14 2v4a2 2 0 0 0 2 2h4"/></svg>
);
const UserIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-user"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
);
const LogOutIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-log-out"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" x2="9" y1="12" y2="12"/></svg>
);
const CheckCircleIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-check-circle"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><path d="m9 11 3 3L22 4"/></svg>
);
const XCircleIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-x-circle"><circle cx="12" cy="12" r="10"/><path d="m15 9-6 6"/><path d="m9 9 6 6"/></svg>
);
const CopyIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-copy"><rect width="14" height="14" x="8" y="8" rx="2" ry="2"/><path d="M4 16c-1.1 0-2-.9-2-2V4c0-1.1.9-2 2-2h10c1.1 0 2 .9 2 2v2"/></svg>
);

const API_BASE_URL = 'http://localhost:8080/api'; // Ensure this matches your Spring Boot backend URL

// Utility function for showing temporary messages
const useMessage = () => {
    const [message, setMessage] = useState(null);
    const [messageType, setMessageType] = useState(''); // 'success', 'error', 'info'

    const showMessage = (msg, type = 'info', duration = 3000) => {
        setMessage(msg);
        setMessageType(type);
        setTimeout(() => {
            setMessage(null);
            setMessageType('');
        }, duration);
    };

    return { message, messageType, showMessage };
};

// Component for displaying messages
const MessageDisplay = ({ message, type }) => {
    if (!message) return null;

    const baseClasses = "fixed top-4 right-4 p-4 rounded-lg shadow-lg z-50 flex items-center gap-2";
    let typeClasses = "";
    let Icon = null;

    switch (type) {
        case 'success':
            typeClasses = "bg-accent-green text-white";
            Icon = CheckCircleIcon;
            break;
        case 'error':
            typeClasses = "bg-error-red text-white";
            Icon = XCircleIcon;
            break;
        case 'info':
        default:
            typeClasses = "bg-primary-blue text-white";
            // No specific icon for info, or a generic one can be added
            break;
    }

    return (
        <div className={`${baseClasses} ${typeClasses}`}>
            {Icon && <Icon className="w-5 h-5" />}
            <span>{message}</span>
        </div>
    );
};

// --- Auth Components ---
const AuthForm = ({ type, onAuthSuccess, showMessage }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState(''); // Added email for registration
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        const endpoint = type === 'login' ? '/auth/login' : '/auth/register';

        try {
            const payload = { username, password };
            if (type === 'register') {
                payload.email = email; // Add email only for registration
            }

            const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
            });

            if (response.ok) {
                if (type === 'login') {
                    const data = await response.json();
                    localStorage.setItem('token', data.token);
                    onAuthSuccess(username);
                    showMessage('Login successful!', 'success');
                } else {
                    showMessage('Registration successful! Please log in.', 'success');
                    setUsername('');
                    setPassword('');
                    setEmail('');
                }
            } else {
                const errorData = await response.json();
                showMessage(errorData.message || 'Authentication failed', 'error');
            }
        } catch (error) {
            console.error('Auth error:', error);
            showMessage('Network error or server unavailable.', 'error');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-md mx-auto">
            <h2 className="text-3xl font-bold text-center mb-6 text-primary-blue">
                {type === 'login' ? 'Login' : 'Register'}
            </h2>
            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block text-text-gray text-sm font-semibold mb-2" htmlFor="username">
                        Username
                    </label>
                    <input
                        type="text"
                        id="username"
                        className="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-primary-blue focus:border-transparent transition duration-200 bg-white"
                        placeholder="Your username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                {type === 'register' && (
                    <div>
                        <label className="block text-text-gray text-sm font-semibold mb-2" htmlFor="email">
                            Email
                        </label>
                        <input
                            type="email"
                            id="email"
                            className="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-primary-blue focus:border-transparent transition duration-200 bg-white"
                            placeholder="your@example.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                )}
                <div>
                    <label className="block text-text-gray text-sm font-semibold mb-2" htmlFor="password">
                        Password
                    </label>
                    <input
                        type="password"
                        id="password"
                        className="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-primary-blue focus:border-transparent transition duration-200 bg-white"
                        placeholder="Your password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button
                    type="submit"
                    className="w-full bg-primary-blue hover:bg-blue-600 text-white font-bold py-3 px-4 rounded-xl focus:outline-none focus:shadow-outline transition duration-200 ease-in-out transform hover:scale-105"
                    disabled={loading}
                >
                    {loading ? 'Processing...' : (type === 'login' ? 'Log In' : 'Register')}
                </button>
            </form>
            <p className="text-center text-text-gray text-sm mt-6">
                {type === 'login' ? (
                    <>
                        Don't have an account?{' '}
                        <button
                            onClick={() => onAuthSuccess(null, 'register')}
                            className="text-primary-blue hover:underline font-semibold"
                        >
                            Register here
                        </button>
                    </>
                ) : (
                    <>
                        Already have an account?{' '}
                        <button
                            onClick={() => onAuthSuccess(null, 'login')}
                            className="text-primary-blue hover:underline font-semibold"
                        >
                            Login here
                        </button>
                    </>
                )}
            </p>
        </div>
    );
};

// --- Main App Component ---
const App = () => {
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [username, setUsername] = useState(null);
    const [currentView, setCurrentView] = useState('send'); // 'send', 'receive', 'login', 'register', 'my-files'
    const { message, messageType, showMessage } = useMessage();

    useEffect(() => {
        const verifyToken = async () => {
            if (token && !username) {
                try {
                    const response = await fetch(`${API_BASE_URL}/files`, {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });

                    if (response.ok) {
                        const decodedUsername = parseJwt(token).sub;
                        setUsername(decodedUsername);
                        setCurrentView('send');
                        showMessage('Welcome back!', 'info');
                    } else {
                        localStorage.removeItem('token');
                        setToken(null);
                        setUsername(null);
                        setCurrentView('login');
                        showMessage('Session expired or invalid. Please log in again.', 'error');
                    }
                } catch (error) {
                    console.error('Token verification error:', error);
                    localStorage.removeItem('token');
                    setToken(null);
                    setUsername(null);
                    setCurrentView('login');
                    showMessage('Could not verify session. Please log in.', 'error');
                }
            } else if (!token && username) {
                handleLogout();
            } else if (!token && !username && currentView !== 'register' && currentView !== 'login') {
                setCurrentView('login');
            }
        };

        verifyToken();
    }, [token, username, currentView]);

    // Helper to decode JWT (simplistic, for username extraction)
    const parseJwt = (token) => {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch (_e) {
            return {};
        }
    };

    const handleAuthSuccess = (loggedInUsername, redirectTo = 'send') => {
        setToken(localStorage.getItem('token'));
        setUsername(loggedInUsername);
        setCurrentView(redirectTo);
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
        setToken(null);
        setUsername(null);
        setCurrentView('login');
        showMessage('Logged out successfully.', 'info');
    };

    const FileUpload = () => {
        const [selectedFile, setSelectedFile] = useState(null);
        const [uploadProgress, setUploadProgress] = useState(0);
        const [uploading, setUploading] = useState(false);
        const [shareLinkResponse, setShareLinkResponse] = useState(null); // Full response object
        const fileInputRef = useRef(null);
        const qrCanvasRef = useRef(null); // Ref for the QR code canvas

        // Effect to generate QR code when shareLinkResponse.shareUrl changes
        useEffect(() => {
            console.log('QR Code useEffect triggered. shareLinkResponse:', shareLinkResponse, 'qrCanvasRef.current:', qrCanvasRef.current);
            if (shareLinkResponse && shareLinkResponse.shareUrl && qrCanvasRef.current) {
                console.log('Attempting to draw QR code for URL:', shareLinkResponse.shareUrl);
                if (typeof QRious !== 'undefined') {
                    const context = qrCanvasRef.current.getContext('2d');
                    qrCanvasRef.current.width = 180;
                    qrCanvasRef.current.height = 180;
                    context.clearRect(0, 0, qrCanvasRef.current.width, qrCanvasRef.current.height);

                    new QRious({
                        element: qrCanvasRef.current,
                        value: shareLinkResponse.shareUrl, // Use the full share URL for QR
                        size: 180,
                        foreground: '#4A90E2',
                        background: 'white',
                        level: 'H'
                    });
                    console.log('QRious instance created successfully for URL:', shareLinkResponse.shareUrl);
                } else {
                    console.error('QRious library (qrious.min.js) not loaded. Please check index.html.');
                }
            }
        }, [shareLinkResponse]); // Dependency changed to shareLinkResponse

        // Effect to reset state when component changes (navigated away from then back)
        useEffect(() => {
            setSelectedFile(null);
            setUploadProgress(0);
            setUploading(false);
            setShareLinkResponse(null); // Clear previous share link response on new file selection
            if (fileInputRef.current) {
                fileInputRef.current.value = ''; // Clear file input visually
            }
        }, [currentView, token]);


        const handleFileChange = (e) => {
            if (e.target.files.length > 0) {
                setSelectedFile(e.target.files[0]);
                setShareLinkResponse(null); // Clear on new file selection
                setUploadProgress(0);
            }
        };

        const handleDragOver = (e) => {
            e.preventDefault();
            e.stopPropagation();
            e.dataTransfer.dropEffect = 'copy';
        };

        const handleDrop = (e) => {
            e.preventDefault();
            e.stopPropagation();
            if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
                setSelectedFile(e.dataTransfer.files[0]);
                setShareLinkResponse(null); // Clear on new file drop
                setUploadProgress(0);
            }
        };

        const handleUpload = async () => {
            if (!selectedFile) {
                showMessage('Please select a file first.', 'info');
                return;
            }
            if (!token) {
                showMessage('You must be logged in to upload files.', 'error');
                setCurrentView('login');
                return;
            }

            setUploading(true);
            setUploadProgress(0);
            setShareLinkResponse(null); // Ensure share link response is cleared at start

            const formData = new FormData();
            formData.append('file', selectedFile);

            try {
                // Step 1: Upload file
                const uploadResponse = await fetch(`${API_BASE_URL}/files/upload`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                    body: formData,
                });

                if (!uploadResponse.ok) {
                    const errorText = await uploadResponse.text();
                    throw new Error(`File upload failed: ${uploadResponse.status} - ${errorText}`);
                }

                const uploadData = await uploadResponse.json();
                showMessage('File uploaded successfully!', 'success');
                setUploadProgress(100);

                // Step 2: Generate share link for the uploaded file
                console.log('Token sent with share link request:', token); // DEBUG LOG
                const shareLinkRawResponse = await fetch(`${API_BASE_URL}/files/${uploadData.fileId}/share`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({}) // Empty body for default share link options
                });

                let responseBody;
                // Read the response body ONCE, regardless of status
                try {
                    responseBody = await shareLinkRawResponse.json();
                } catch (parseError) {
                    console.warn("Failed to parse share link response as JSON. Attempting to read as text.", parseError);
                    responseBody = await shareLinkRawResponse.text();
                }

                // Now check the response status
                if (!shareLinkRawResponse.ok) {
                    let errorDetails = `Failed to generate share link: ${shareLinkRawResponse.status}`;
                    if (responseBody) {
                        errorDetails += ` - ${typeof responseBody === 'object' ? JSON.stringify(responseBody) : responseBody}`;
                    }
                    // Specific handling for 401 Unauthorized
                    if (shareLinkRawResponse.status === 401) {
                        showMessage('Session expired. Please log in again.', 'error');
                        handleLogout(); // Trigger logout immediately on 401
                    }
                    throw new Error(errorDetails);
                }

                // If response is OK, then proceed
                console.log('Raw shareLinkResponse received (from backend):', responseBody);
                console.log('responseBody.uniqueToken (from backend):', responseBody.uniqueToken);
                console.log('responseBody.shareUrl (from backend):', responseBody.shareUrl);


                // Only set shareLinkResponse if uniqueToken is a non-empty string and shareUrl is present
                if (typeof responseBody.uniqueToken === 'string' && responseBody.uniqueToken.length > 0 && responseBody.shareUrl) {
                    setShareLinkResponse(responseBody); // Set the entire response object
                    showMessage('Share link generated!', 'success');
                } else {
                    throw new Error("Share token or URL not found in response or is empty.");
                }

            }
             catch (error) {
                console.error('Upload or share link error:', error);
                showMessage(error.message || 'An error occurred during upload or link generation.', 'error');
                setUploadProgress(0);
            } finally {
                setUploading(false);
            }
        };


        return (
            <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-2xl mx-auto flex flex-col items-center">
                <h2 className="text-3xl font-bold mb-6 text-primary-blue flex items-center gap-2">
                    <UploadIcon /> Send Files
                </h2>

                <div
                    className="border-2 border-dashed border-primary-blue rounded-xl p-10 w-full text-center cursor-pointer transition-colors duration-200 hover:bg-secondary-gray"
                    onDragOver={handleDragOver}
                    onDrop={handleDrop}
                    onClick={() => fileInputRef.current.click()}
                >
                    <UploadIcon className="mx-auto w-16 h-16 text-primary-blue mb-4" />
                    <p className="text-lg text-text-gray font-semibold">Drag & Drop your file here</p>
                    <p className="text-sm text-gray-500 mb-4">or click to browse</p>
                    <input
                        type="file"
                        ref={fileInputRef}
                        onChange={handleFileChange}
                        className="hidden"
                    />
                </div>

                {selectedFile && (
                    <div className="mt-6 w-full text-center">
                        <p className="text-lg font-semibold text-gray-800">Selected file: <span className="text-primary-blue">{selectedFile.name}</span></p>
                        <p className="text-sm text-gray-600">Size: {(selectedFile.size / (1024 * 1024)).toFixed(2)} MB</p>
                        {uploading && (
                            <div className="w-full bg-secondary-gray rounded-full h-2.5 mt-4">
                                <div
                                    className="bg-accent-green h-2.5 rounded-full"
                                    style={{ width: `${uploadProgress}%` }}
                                ></div>
                            </div>
                        )}
                    </div>
                )}

                <button
                    onClick={handleUpload}
                    className="mt-8 w-full bg-accent-green hover:bg-green-600 text-white font-bold py-3 px-4 rounded-xl focus:outline-none focus:shadow-outline transition duration-200 ease-in-out transform hover:scale-105"
                    disabled={uploading || !selectedFile}
                >
                    {uploading ? 'Uploading...' : 'Upload & Get Link'}
                </button>

                {/* Conditional rendering based on shareLinkResponse presence */}
                {shareLinkResponse && shareLinkResponse.uniqueToken && shareLinkResponse.shareUrl && (
                    <div className="mt-8 w-full bg-secondary-gray p-6 rounded-xl shadow-md flex flex-col items-center">
                        <p className="text-xl font-bold text-primary-blue mb-3">Your Share Link:</p>
                        <div className="flex items-center bg-white border border-primary-blue rounded-lg overflow-hidden w-full max-w-sm">
                            <input
                                type="text"
                                readOnly
                                value={shareLinkResponse.shareUrl} // Display the full share URL
                                className="flex-grow p-3 text-center text-gray-800 font-mono text-lg outline-none bg-white rounded-l-lg"
                            />
                            <button
                                onClick={() => {
                                    const tempInput = document.createElement('input');
                                    tempInput.value = shareLinkResponse.shareUrl; // Copy the full share URL
                                    document.body.appendChild(tempInput);
                                    tempInput.select();
                                    document.execCommand('copy');
                                    document.body.removeChild(tempInput);
                                    showMessage('Link copied to clipboard!', 'info');
                                }}
                                className="p-3 bg-primary-blue hover:bg-blue-600 text-white transition duration-200 rounded-r-lg flex items-center justify-center"
                                title="Copy to clipboard"
                            >
                                <CopyIcon className="w-5 h-5" />
                            </button>
                        </div>

                        {/* Display Share Code separately if desired, or just use the URL */}
                        <p className="mt-4 text-sm text-gray-600 text-center">
                            Or share just the code: <span className="font-mono text-primary-blue font-bold">{shareLinkResponse.uniqueToken}</span>
                            <button
                                onClick={() => {
                                    const tempInput = document.createElement('input');
                                    tempInput.value = shareLinkResponse.uniqueToken;
                                    document.body.appendChild(tempInput);
                                    tempInput.select();
                                    document.execCommand('copy');
                                    document.body.removeChild(tempInput);
                                    showMessage('Code copied to clipboard!', 'info');
                                }}
                                className="ml-2 px-2 py-1 border border-gray-300 rounded-md text-sm hover:bg-gray-100"
                                title="Copy share code"
                            >
                                <CopyIcon className="w-4 h-4 inline-block align-middle" />
                            </button>
                        </p>


                        {/* QR Code Display Area */}
                        <div className="mt-6 p-4 bg-white rounded-lg shadow-inner">
                            <canvas ref={qrCanvasRef} className="w-[180px] h-[180px] block mx-auto"></canvas> {/* Fixed size for canvas */}
                        </div>

                        <p className="mt-4 text-sm text-gray-600 text-center">{shareLinkResponse.message || "Share this link or QR code with others."}</p>
                    </div>
                )}
            </div>
        );
    };

    const FileDownload = () => {
        const [shareCode, setShareCode] = useState('');
        const [downloading, setDownloading] = useState(false);
        const [fileMetadata, setFileMetadata] = useState(null); // To show file info before download

        const handleDownload = async () => {
            if (!shareCode) {
                showMessage('Please enter a share code.', 'info');
                return;
            }

            setDownloading(true);
            setFileMetadata(null);

            try {
                const response = await fetch(`${API_BASE_URL}/share/download/${shareCode}`);

                if (response.ok) {
                    const contentDisposition = response.headers.get('Content-Disposition');
                    let filename = 'downloaded_file';
                    if (contentDisposition && contentDisposition.indexOf('filename=') !== -1) {
                        filename = contentDisposition.split('filename=')[1].split(';')[0].replace(/"/g, '');
                    }

                    const blob = await response.blob();
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = filename;
                    document.body.appendChild(a);
                    a.click();
                    a.remove();
                    window.URL.revokeObjectURL(url);
                    showMessage('File downloaded successfully!', 'success');
                } else {
                    const errorText = await response.text();
                    try {
                        const errorData = JSON.parse(errorText);
                        throw new Error(errorData.message || 'An error occurred during download.');
                    } catch (_parseError) {
                        throw new Error(`Download failed: ${response.status} - ${errorText || 'Unknown error'}`);
                    }
                }
            } catch (error) {
                console.error('Download error:', error);
                showMessage(error.message || 'An error occurred during download.', 'error');
            } finally {
                setDownloading(false);
                setShareCode('');
            }
        };

        const handleShareCodeChange = async (e) => {
            const code = e.target.value;
            setShareCode(code);
            setFileMetadata(null);

            if (code.length === 36) { // Assuming UUID length
                try {
                    const response = await fetch(`${API_BASE_URL}/share/details/${code}`, {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });
                    if (response.ok) {
                        const data = await response.json();
                        setFileMetadata({
                            fileName: data.originalFileName,
                            size: data.sizeBytes || 0,
                            expiresAt: data.expiresAt,
                            maxDownloads: data.maxDownloads,
                            currentDownloads: data.currentDownloads
                        });
                    } else if (response.status === 401 && token) {
                         console.warn("Not authorized to view details of this specific share link, but can still try download.");
                         setFileMetadata(null);
                    } else {
                         setFileMetadata(null);
                    }
                } catch (error) {
                    console.warn("Error fetching share link metadata:", error);
                }
            }
        };

        return (
            <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-2xl mx-auto flex flex-col items-center">
                <h2 className="text-3xl font-bold mb-6 text-primary-blue flex items-center gap-2">
                    <DownloadIcon /> Receive Files
                </h2>

                <div className="w-full text-center">
                    <label className="block text-text-gray text-sm font-semibold mb-2" htmlFor="shareCode">
                        Enter Share Code
                    </label>
                    <input
                        type="text"
                        id="shareCode"
                        className="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-primary-blue focus:border-transparent transition duration-200 text-center text-xl font-mono tracking-widest bg-white"
                        placeholder="e.g., d1c2f3g4-..."
                        value={shareCode}
                        onChange={handleShareCodeChange}
                        required
                    />
                </div>

                {fileMetadata && (
                    <div className="mt-6 w-full text-center bg-secondary-gray p-4 rounded-lg">
                        <p className="text-lg font-semibold text-gray-800">File Name: <span className="text-primary-blue">{fileMetadata.fileName || 'N/A'}</span></p>
                        {fileMetadata.size > 0 && <p className="text-sm text-gray-600">Size: {(fileMetadata.size / (1024 * 1024)).toFixed(2)} MB</p>}
                        {fileMetadata.expiresAt && <p className="text-sm text-gray-600">Expires: {new Date(fileMetadata.expiresAt).toLocaleString()}</p>}
                        {fileMetadata.maxDownloads && <p className="text-sm text-gray-600">Downloads Left: {fileMetadata.maxDownloads - fileMetadata.currentDownloads} / {fileMetadata.maxDownloads}</p>}
                    </div>
                )}

                <button
                    onClick={handleDownload}
                    className="mt-8 w-full bg-primary-blue hover:bg-blue-600 text-white font-bold py-3 px-4 rounded-xl focus:outline-none focus:shadow-outline transition duration-200 ease-in-out transform hover:scale-105"
                    disabled={downloading || !shareCode}
                >
                    {downloading ? 'Downloading...' : 'Download File'}
                </button>
            </div>
        );
    };

    const MyFiles = () => {
        const [files, setFiles] = useState([]);
        const [loading, setLoading] = useState(true);
        const [error, setError] = useState(null);

        useEffect(() => {
            const fetchMyFiles = async () => {
                if (!token) {
                    setError('Not authenticated.');
                    setLoading(false);
                    return;
                }
                try {
                    const response = await fetch(`${API_BASE_URL}/files`, {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });
                    if (response.ok) {
                        const data = await response.json();
                        setFiles(data);
                    } else if (response.status === 401) {
                        showMessage('Session expired. Please log in again.', 'error');
                        handleLogout();
                    }
                    else {
                        const errorText = await response.text();
                        try {
                            const errorData = JSON.parse(errorText);
                            throw new Error(errorData.message || `Failed to fetch files: ${response.status} - Unknown error`);
                        } catch (_parseError) {
                            throw new Error(`Failed to fetch files: ${response.status} - ${errorText}`);
                        }
                    }
                } catch (err) {
                    console.error('Error fetching my files:', err);
                    setError(err.message);
                } finally {
                    setLoading(false);
                }
            };
            fetchMyFiles();
        }, [token]);

        const handleDeleteFile = async (fileId) => {
            if (!window.confirm('Are you sure you want to delete this file?')) {
                return;
            }
            try {
                const response = await fetch(`${API_BASE_URL}/files/${fileId}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.ok) {
                    showMessage('File deleted successfully.', 'success');
                    setFiles(files.filter(file => file.id !== fileId));
                } else if (response.status === 401) {
                    showMessage('Session expired. Please log in again.', 'error');
                    handleLogout();
                } else {
                    const errorText = await response.text();
                    try {
                        const errorData = JSON.parse(errorText);
                        throw new Error(errorData.message || `Failed to delete file: ${response.status} - Unknown error`);
                    } catch (_parseError) {
                        throw new Error(`Failed to delete file: ${response.status} - ${errorText}`);
                    }
                }
            } catch (err) {
                console.error('Error deleting file:', err);
                showMessage(err.message || 'Error deleting file.', 'error');
            }
        };

        if (loading) {
            return (
                <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-2xl mx-auto text-center text-lg text-primary-blue">
                    Loading your files...
                </div>
            );
        }

        if (error) {
            return (
                <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-2xl mx-auto text-center text-error-red">
                    Error: {error}
                </div>
            );
        }

        return (
            <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-4xl mx-auto">
                <h2 className="text-3xl font-bold mb-6 text-primary-blue flex items-center gap-2">
                    <FileIcon /> My Uploaded Files
                </h2>
                {files.length === 0 ? (
                    <p className="text-center text-lg text-text-gray">You haven't uploaded any files yet.</p>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="min-w-full bg-white border border-gray-200 rounded-lg">
                            <thead className="bg-secondary-gray">
                                <tr>
                                    <th className="py-3 px-4 text-left text-sm font-semibold text-gray-600 rounded-tl-lg">File Name</th>
                                    <th className="py-3 px-4 text-left text-sm font-semibold text-gray-600">Size</th>
                                    <th className="py-3 px-4 text-left text-sm font-semibold text-gray-600">Type</th>
                                    <th className="py-3 px-4 text-left text-sm font-semibold text-gray-600">Upload Date</th>
                                    <th className="py-3 px-4 text-left text-sm font-semibold text-gray-600 rounded-tr-lg">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {files.map(file => (
                                    <tr key={file.id} className="border-b border-gray-100 last:border-b-0 hover:bg-gray-50">
                                        <td className="py-3 px-4 text-gray-800 flex items-center gap-2">
                                            <FileIcon className="w-5 h-5 text-gray-500" /> {file.originalName}
                                        </td>
                                        <td className="py-3 px-4 text-gray-700">{(file.sizeBytes / (1024 * 1024)).toFixed(2)} MB</td>
                                        <td className="py-3 px-4 text-gray-700">{file.mimeType || 'N/A'}</td>
                                        <td className="py-3 px-4 text-gray-700">{new Date(file.uploadTimestamp).toLocaleDateString()}</td>
                                        <td className="py-3 px-4">
                                            <div className="flex gap-2">
                                                <a
                                                    href={file.downloadUri}
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                    className="inline-flex items-center px-3 py-1.5 border border-primary-blue text-primary-blue rounded-lg text-sm font-medium hover:bg-primary-blue hover:text-white transition duration-200"
                                                    download
                                                >
                                                    <DownloadIcon className="w-4 h-4 mr-1" /> Download
                                                </a>
                                                <button
                                                    onClick={() => handleDeleteFile(file.id)}
                                                    className="inline-flex items-center px-3 py-1.5 border border-error-red text-error-red rounded-lg text-sm font-medium hover:bg-error-red hover:text-white transition duration-200"
                                                >
                                                    <XCircleIcon className="w-4 h-4 mr-1" /> Delete
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        );
    };


    const renderContent = () => {
        switch (currentView) {
            case 'login':
            case 'register':
                return <AuthForm type={currentView} onAuthSuccess={handleAuthSuccess} showMessage={showMessage} />;
            case 'send':
                return <FileUpload />;
            case 'receive':
                return <FileDownload />;
            case 'my-files':
                return <MyFiles />;
            default:
                return null;
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-primary-blue to-blue-300 font-sans text-gray-900 flex flex-col items-center py-12 px-4 sm:px-6 lg:px-8">
            <MessageDisplay message={message} type={messageType} />

            <header className="w-full max-w-4xl bg-white p-6 rounded-xl shadow-lg mb-8 flex justify-between items-center flex-wrap gap-4">
                <h1 className="text-4xl font-extrabold text-primary-blue flex items-center gap-3">
                    <ShareIcon className="w-10 h-10" /> Secure File Sharer
                </h1>
                {username ? (
                    <nav className="flex items-center gap-4 text-lg font-medium">
                        <span className="text-text-gray flex items-center gap-2">
                            <UserIcon className="w-5 h-5" /> {username}
                        </span>
                        <button
                            onClick={() => setCurrentView('send')}
                            className={`px-4 py-2 rounded-lg transition duration-200 ${currentView === 'send' ? 'bg-primary-blue text-white' : 'text-primary-blue hover:bg-secondary-gray'}`}
                        >
                            Send
                        </button>
                        <button
                            onClick={() => setCurrentView('receive')}
                            className={`px-4 py-2 rounded-lg transition duration-200 ${currentView === 'receive' ? 'bg-primary-blue text-white' : 'text-primary-blue hover:bg-secondary-gray'}`}
                        >
                            Receive
                        </button>
                        <button
                            onClick={() => setCurrentView('my-files')}
                            className={`px-4 py-2 rounded-lg transition duration-200 ${currentView === 'my-files' ? 'bg-primary-blue text-white' : 'text-primary-blue hover:bg-secondary-gray'}`}
                        >
                            My Files
                        </button>
                        <button
                            onClick={handleLogout}
                            className="px-4 py-2 rounded-lg bg-error-red hover:bg-red-600 text-white transition duration-200 flex items-center gap-1"
                        >
                            <LogOutIcon className="w-5 h-5" /> Logout
                        </button>
                    </nav>
                ) : (
                    <nav className="flex items-center gap-4 text-lg font-medium">
                        <button
                            onClick={() => setCurrentView('login')}
                            className={`px-4 py-2 rounded-lg transition duration-200 ${currentView === 'login' ? 'bg-primary-blue text-white' : 'text-primary-blue hover:bg-secondary-gray'}`}
                        >
                            Login
                        </button>
                        <button
                            onClick={() => setCurrentView('register')}
                            className={`px-4 py-2 rounded-lg transition duration-200 ${currentView === 'register' ? 'bg-primary-blue text-white' : 'text-primary-blue hover:bg-secondary-gray'}`}
                        >
                            Register
                        </button>
                    </nav>
                )}
            </header>

            <main className="w-full flex-grow flex items-center justify-center">
                {renderContent()}
            </main>

            <footer className="mt-12 text-center text-white text-sm opacity-80">
                &copy; {new Date().getFullYear()} Secure File Sharer. All rights reserved.
            </footer>
        </div>
    );
};

export default App;
