import React from 'react';
import { format } from 'date-fns';
import { cs } from 'date-fns/locale';
import { Info } from 'lucide-react';
import { Link } from 'react-router-dom'; // Importujte Link pro vytvoření odkazu

const InfoSection = ({ capsule }) => {
    const formattedDate = format(new Date(capsule.openDate), 'd. MMMM yyyy', { locale: cs });

    return (
        <section className="flex flex-col justify-center mt-6 w-full max-md:max-w-full">
            <div className="flex flex-col p-6 w-full bg-gray-50 rounded-lg min-h-[192px] max-md:px-5 max-md:max-w-full">
                <h3 className="w-full text-base font-semibold text-gray-900 max-md:max-w-full">Základní informace</h3>
                <div className="flex flex-col mt-4 w-full max-md:max-w-full">
                    <div className="flex flex-wrap gap-4">
                        <InfoItem label="Název kapsle" value={capsule.title} />
                        <InfoItem label="Popis kapsle" value={capsule.description} />
                        <InfoItem label="Datum otevření" value={formattedDate} /> {/* Formátovaný datum  */}
                    </div>
                    <div className="flex flex-wrap gap-4 mt-4">
                        <InfoItem label="Privátní" value={capsule.isPrivate ? "Ano" : "Ne"} />
                    </div>
                    <div className="flex flex-wrap gap-4 mt-4">
                        <InfoItem
                            label="Otevření QR kódem"
                            value={capsule.hasQRCode
                                ? <Link to="/qrcode"  className="text-blue-500 hover:underline">Ano</Link>
                                : "Ne"
                            }
                        />
                        <InfoItem label="Otevření pomocí GPS" value={capsule.geolocation ? "Ano" : "Ne"} />
                        <InfoItem label="Počet přispěvatelů" value={capsule.contributors.length} />
                    </div>
                </div>
            </div>
        </section>
    );
};

const InfoItem = ({ label, value }) => (
    <div className="flex flex-col flex-1 grow shrink-0 basis-0 min-h-[44px] w-full">
        <div className="w-full text-sm leading-none text-gray-500">{label}</div>
        <div className="w-full text-base font-semibold text-black break-words">{value}</div> {/* Zajišťujeme zalomení textu */}
    </div>
);

export default InfoSection;


