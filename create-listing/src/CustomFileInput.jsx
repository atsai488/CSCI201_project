import 'bootstrap/dist/css/bootstrap.min.css';
import './CustomFileInput.css'

import { useRef, useState } from 'react';

export default function CustomFileInput(props) {
    const inputFile = useRef(null)
    const [imageSrc, setImageSrc] = useState(null)
    
    function handleCLick (){
        inputFile.current.click()
    }
    
    function handleFileChange (e) {
        const file = e.target.files[0]
        if (file) {
            setImageSrc(URL.createObjectURL(file))
        }
        props.onChange(file)
    }
    return (
        <div className="col-3 mb-3">
            <input 
                type="file" 
                id='images'
                ref={inputFile} 
                className="d-none" 
                accept="image/*"
                onChange={handleFileChange}
                name='images'
                required
            />
            <div 
                className="custom-file border rounded-3"
                onClick={handleCLick}
                style={{
                    backgroundColor: imageSrc ? 'transparent' : '#fff',
                    backgroundImage: imageSrc ? `url(${imageSrc})` : 'none'
                }}
            >
                {!imageSrc && <span> + </span>}   
            </div>
            <div className="invalid-feedback py-2 text-center">
                Please provide a image for your product
            </div>
            <div className="valid-feedback py-2 text-center">
                Looks good!
            </div>
        </div>
    )
}