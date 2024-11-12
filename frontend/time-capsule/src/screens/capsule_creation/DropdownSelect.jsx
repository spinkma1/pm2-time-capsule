import React from 'react';

const DropdownSelect = ({ label, value, options, onChange }) => {
  return (
    <div className="flex flex-wrap max-w-2xl">
      <label 
        htmlFor="counterSelect" 
        className="text-sm font-semibold leading-5 text-gray-700 mr-2 flex items-center" 
      >
        {label}
      </label>
      <div className="flex justify-center items-start self-start px-4 py-2.5 text-base text-gray-400 bg-white rounded-lg border border-solid w-[100px]">
        <select
          id="counterSelect"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="flex-1 bg-transparent text-gray-900 cursor-pointer"
          aria-label={label}
        >
          {options.map((option) => (
            <option key={option} value={option}>
              {option}
            </option>
          ))}
        </select>
      </div>
    </div>
  );
};

export default DropdownSelect;

