import React from 'react';
import { Info, Check } from 'lucide-react';

const Warning = () => {
  return (
    <section className="flex flex-col justify-center pt-6 mt-6 w-full min-h-[192px] max-md:max-w-full">
      <div className="flex flex-col justify-center p-6 w-full bg-yellow-50 rounded-lg min-h-[168px] max-md:px-5 max-md:max-w-full">
        <div className="flex flex-wrap items-center w-full max-md:max-w-full">
          <div className="flex flex-row w-full ml-2">
            <div className="flex items-start justify-start pt-1 pr-3 w-9 min-h-[28px]">
                <Info size={24} />
            </div>
            
            <div className="flex flex-col mt-2 w-full">
            <h4 className="w-full text-base font-semibold text-yellow-900">Před uzavřením kapsle zkontrolujte:</h4>
              <WarningItem text="Všechny soubory byly úspěšně nahrány" />
              <WarningItem text="Všichni přispěvatelé byli pozváni" />
              <WarningItem text="Datum otevření je správně nastaveno" />
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

const WarningItem = ({ text }) => (
  <div className="flex items-center w-full mt-2">
    <div className="flex flex-col items-center justify-center pr-3 my-auto w-8 min-h-[24px]">
      <Check size={24} />
    </div>
    <div className="flex-grow my-auto text-base text-yellow-800">{text}</div>
  </div>
);

export default Warning;
