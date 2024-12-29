import React from 'react';
import { useState } from "react";

const Confirmation = ({ onClick }) => {
    const [isChecked, setIsChecked] = useState(false);

    const handleCheckboxChange = (event) => {
        setIsChecked(event.target.checked);
    };

    const handleButtonClick = () => {
        if (isChecked) {
            onClick();
        } else {
            alert("Potvrdte prosím, že jste zkontrolovali všechny nahrané soubory a nastavení kapsle.");
        }
    };

    return (
        <section className="flex flex-col justify-center pt-8 mt-6 w-full min-h-[232px] max-md:max-w-full">
            <div className="flex flex-col p-6 w-full bg-gray-50 rounded-lg min-h-[200px] max-md:px-5 max-md:max-w-full">
                <h3 className="w-full text-base font-semibold text-gray-900 max-md:max-w-full">
                    Finální potvrzení
                </h3>
                <div className="flex flex-col mt-4 w-full max-md:max-w-full">
                    <div className="flex flex-wrap items-start w-full max-md:max-w-full">
                        <div className="flex flex-row grow shrink pt-1 w-2.5 min-h-[20px]">
                            <input
                                type="checkbox"
                                id="finalConfirmation"
                                className="flex bg-white rounded border border-solid border-neutral-500 h-[13px] min-h-[13px] w-[13px] my-2"
                                onChange={handleCheckboxChange}
                            />
                            <label
                                htmlFor="finalConfirmation"
                                className="flex flex-row pl-3 text-base leading-6 text-gray-700 min-w-[240px] max-md:max-w-full"
                            >
                                <span className="pr-0.5 max-md:max-w-full">
                                    Potvrzuji, že jsem zkontroloval/a všechny nahrané soubory a nastavení kapsle. Rozumím tomu, že po uzavření kapsle
                                    nebude možné přidávat další obsah.
                                </span>
                            </label>
                        </div>
                    </div>
                    <div className="flex flex-row gap-4 justify-center items-center px-6 mt-4 w-full max-w-[847px] max-md:max-w-full">
                        <button
                            className="py-3 px-8 text-white bg-blue-900 hover:bg-blue-800 rounded-lg min-w-[240px] w-[350px] max-md:px-5"
                            onClick={handleButtonClick}
                        >
                            Dokončit
                        </button>
                    </div>
                </div>
            </div>
        </section>
    );
};

export default Confirmation;

