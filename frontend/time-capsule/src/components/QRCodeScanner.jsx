import React, { Component } from 'react';
import QrReader from 'react-qr-scanner';

class QRCodeScanner extends Component {
    constructor(props) {
        super(props);
        this.state = {
            delay: 300,
            result: 'Žádný',
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
        const { result, scanError, delay } = this.state;

        return (
            <div>
                <div className="w-full">
                    {/* Always use rear camera */}
                    <QrReader delay={delay} style={{ width: '50%' }} onError={this.componentDidUpdatehandleError} onScan={this.handleScan} constraints={{
                        audio: false,
                        video: { facingMode: "environment" }
                    }} />
                </div>

                {scanError && <p className="text-red-500 text-sm">{scanError}</p>}
                <p>Výsledený kód: {result}</p>
            </div>
        );
    }
}

export default QRCodeScanner;


