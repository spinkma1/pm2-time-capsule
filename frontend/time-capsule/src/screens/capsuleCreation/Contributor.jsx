import React from 'react';
import { X } from 'lucide-react';

function Contributor({ id, email, status, initial, canDelete, onDelete }) {
    const statusColors = {
        "Neaktivní": { bg: "bg-yellow-100", text: "text-yellow-700" },
        "Aktivní": { bg: "bg-green-100", text: "text-green-700" },
    };

    return (
        <div key={id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
            <div className="flex items-center">
                <div className="w-10 h-10 bg-blue-900 text-white rounded-full flex items-center justify-center mr-3 hidden sm:flex">
                    {initial}
                </div>
                <div className="font-small text-sm sm:text-lg">
                    <div className="font-medium">{email}</div>
                </div>
            </div>
            
            <div className="flex items-center space-x-2">
                <span className={`px-2 py-1 text-sm leading-none ${statusColors[status].text} ${statusColors[status].bg} rounded-full`}>
                    {status}
                </span>
                {canDelete && (
                    <button 
                    className="text-gray-400 hover:text-gray-600"
                    onClick={() => onDelete(id)}>
                        <X size={16} />
                    </button>
                )}
            </div>
        </div>
    );
}

export default Contributor;


