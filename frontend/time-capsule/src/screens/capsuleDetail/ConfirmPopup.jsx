import React from "react";

const ConfirmPopup = ({ isOpen, onClose, onConfirm, text }) => {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 bg-black bg-opacity-50 flex items-center justify-center">
            <div className="bg-white rounded-lg shadow-lg w-96 p-6 relative">
                <button
                    onClick={onClose}
                    className="absolute top-4 right-4 text-gray-500 hover:text-gray-700"
                >
                    ✕
                </button>
                <h2 className="text-xl font-semibold text-gray-800 mb-4">Potvrzení</h2>
                <p className="text-sm text-gray-600 mb-6">{text}</p>
                <div className="flex justify-end space-x-4">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300"
                    >
                        Ne
                    </button>
                    <button
                        onClick={onConfirm}
                        className="px-4 py-2 bg-blue-900 text-white rounded-lg hover:bg-blue-700"
                    >
                        Ano
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmPopup;
