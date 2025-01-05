import React from 'react';
import { Info } from 'lucide-react';

function InfoBox({ title, description }) {
    return (
      <section className="flex items-center p-4 max-w-4xl bg-blue-50 rounded-lg">
        {/* Icon on the left */}
        <Info size={50}/>
        
        {/* Title and description on the same row */}
        <div className="flex flex-col text-sm leading-none text-blue-900 ml-4">
          <h2 className="font-semibold">{title}</h2>
          <p className="mt-1">{description}</p>
        </div>
      </section>
    );
  }
  
  export default InfoBox;
  