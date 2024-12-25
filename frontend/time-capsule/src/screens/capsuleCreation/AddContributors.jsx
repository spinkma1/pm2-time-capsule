import React, { useState, useEffect } from "react";
import { ArrowLeft, Check } from "lucide-react";
import { useNavigate } from "react-router-dom";
import Contributor from "../../components/capsulecreation/Contributor";
import InfoBox from "../../components/capsulecreation/InfoBox";
import DropdownSelect from "../../components/capsulecreation/DropdownSelect";

const AddContributor = ({ capsule }) => {
    const navigate = useNavigate();
    console.log(capsule);
    const [emailForm, setEmailForm] = useState({ email: "" });
    const [emailErrors, setEmailErrors] = useState({});
    const [formData, setFormData] = useState({
        contributors: [],
    });

    useEffect(() => {
        if (capsule?.contributors) {
            setFormData({
                contributors: JSON.parse(JSON.stringify(capsule.contributors)),
            });
        }
    }, [capsule]);


    const handleDelete = (id) => {
        const updatedContributors = formData.contributors.filter(contributor => contributor.id !== id);
        setFormData({ ...formData, contributors: updatedContributors });
    };


    // Validace emailu
    const validateEmailForm = (emailData) => {
        const errors = {};
        if (!emailData.email || !/\S+@\S+\.\S+/.test(emailData.email)) {
            errors.email = "Zadejte platnou e-mailovou adresu";
        }
        setEmailErrors(errors);
        return Object.keys(errors).length === 0;
    };


    const handleEmailSubmit = (e) => {
        e.preventDefault();
        const newEmail = emailForm.email.trim();
        if (validateEmailForm({ email: newEmail })) {
            const newContributor = {
                id: formData.contributors.length + 1,
                email: newEmail,
                status: "Neaktivní",
                avatar: newEmail.split('@')[0].slice(0, 2).toUpperCase(),
            };
            const updatedCapsule = {
                ...capsule,
                contributors: [...capsule.contributors, newContributor],
            };
            setFormData((prev) => ({
                ...prev,
                contributors: [...prev.contributors, newContributor],
            }));
            setEmailForm({ email: '' });
            setEmailErrors({});
        }
    };

    const handleSubmit = () => {
        const updatedCapsule = {
            ...capsule,
            contributors: formData.contributors,
        };
    }

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm">
                <div className="container mx-auto px-4 py-4">
                    <button
                        onClick={() => navigate('/capsuleDetail')}
                        className="flex items-center text-gray-600 hover:text-blue-900"
                    >
                        <ArrowLeft size={20} className="mr-2" />
                        Zpět
                    </button>
                </div>
            </header>

            {/* Main */}
            <main className="container mx-auto px-4 py-8">
                <div className="max-w-3xl mx-auto">
                    <div className="bg-white rounded-lg shadow-sm p-6">
                        <div className="space-y-6">
                            <h2 className="text-2xl font-bold text-gray-900">Pozvat přispěvatele</h2>
                            {/* Formulář pro email */}
                            <div className="flex flex-col space-y-2">
                                <input
                                    type="email"
                                    placeholder="Zadejte email přispěvatele"
                                    value={emailForm.email}
                                    onChange={(e) =>
                                        setEmailForm({ ...emailForm, email: e.target.value })
                                    }
                                    className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                />
                                {emailErrors.email && (
                                    <p className="text-red-500 text-sm mt-1">{emailErrors.email}</p>
                                )}
                                <button
                                    onClick={handleEmailSubmit}
                                    className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-500 flex items-center max-w-max"
                                >
                                    <Check size={20} className="mr-1" />
                                    Potvrdit
                                </button>
                            </div>

                            {/* Seznam přispěvatelů */}
                            <div className="mt-6">
                                {formData.contributors.map((contributor) => (
                                    <Contributor
                                        key={contributor.id}
                                        id={contributor.id}
                                        email={contributor.email}
                                        status="Neaktivní"
                                        initial={contributor.avatar}
                                        canDelete={true}
                                        onDelete={handleDelete}
                                    />
                                ))}
                            </div>

                            <InfoBox
                                title="Správa přispěvatelů"
                                description={`Přispěvatelé mohou přidávat obsah do kapsle až do jejího uzavření. Každý přispěvatel může přidat maximálně ${capsule.maxItems} souborů. Lidé, kterým je přidělen přístup k vaší kapsi, musí být registrováni na této platformě.`}
                            />
                        

                            {/* Navigační tlačítka */}
                            <div className="flex justify-end mt-6">
                                <button
                                    onClick={handleSubmit}
                                    className="px-4 py-2 bg-blue-900 text-white rounded-lg hover:bg-blue-800"
                                >
                                    Potvrdit
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default AddContributor;

