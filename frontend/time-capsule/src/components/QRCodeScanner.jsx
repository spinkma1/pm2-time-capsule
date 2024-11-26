import React, { Component } from 'react';
import QrScanner from 'react-qr-scanner';

class QRCodeScanner extends Component {
    constructor(props){
        super(props);
        this.state = {
            delay: 300, 
            result: 'Žádný', 
            camera: 'rear', 
            scanError: '', 
        };

        this.handleScan = this.handleScan.bind(this);
        this.handleError = this.handleError.bind(this);
    }

    handleScan(data) {
        if (data) {
            this.setState({
                result: data.text, 
                scanError: '',
            });
        }
    }

    handleError(err) {
        console.error(err);
        this.setState({
            scanError: 'Failed to scan QR code. Please try again.'
        });
    }

    render() {
        const { result, camera, scanError, delay } = this.state;
        
        return (
            <div className="space-y-6">
            

                <div className="w-full justify-end">
                  
                    <QrScanner
                        key={camera}  
                        delay={delay} 
                        style={{ width: '75%' }}
                        facingMode={"rear"}  
                        onError={this.handleError} 
                        onScan={this.handleScan}
                    />
                </div>

                {scanError && <p className="text-red-500 text-sm">{scanError}</p>}
                <p>Výsledený kód: {result}</p> 
            </div>
        );
    }
}

export default QRCodeScanner;


