import React from 'react';
import { Copy } from 'lucide-react';

function CopyLinkButton({ valueToCopy }) {
  // Funkce pro kopírování do schránky
  const handleCopy = () => {
    if (valueToCopy) {
      navigator.clipboard.writeText(valueToCopy).then(() => {
        alert("Odkaz byl zkopírován do schránky!");
      }).catch(err => {
        console.error("Chyba při kopírování:", err);
      });
    }
  };

  return (
    <button 
      onClick={handleCopy} 
      className="flex items-center self-stretch my-auto"
      aria-label="Kopírovat odkaz pro přispěvatele"
    >
      <div className="flex flex-col items-start self-stretch pr-1 my-auto w-5 min-h-[16px]">
        <div className="flex overflow-hidden flex-col justify-center w-4 min-h-[16px]">
          <Copy size={16} />
        </div>
      </div>
      <span className="self-stretch my-auto text-sm leading-none text-center text-blue-900">
        Kopírovat odkaz pro přispěvatele
      </span>
    </button>
  );
}

export default CopyLinkButton;
