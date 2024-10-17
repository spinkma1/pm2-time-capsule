package cz.cvut.fel.pm2.carrentalservice.persistence;

import cz.cvut.fel.pm2.carrentalservice.enums.DataType;
import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name = "T_CONTENT")
public class Content extends AbstractEntity{

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type")
    private DataType dataType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_of_upload")
    private Date dateOfUpload;

    @Column(name = "data")
    private byte[] data;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "capsule_id")
    private Capsule capsule;

    public Content(DataType dataType, Date dateOfUpload, byte[] data, String name, String url, Capsule capsule) {
        this.dataType = dataType;
        this.dateOfUpload = dateOfUpload;
        this.data = data;
        this.name = name;
        this.url = url;
        this.capsule = capsule;
    }
    public Content() {
    }
    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Date getDateOfUpload() {
        return dateOfUpload;
    }

    public void setDateOfUpload(Date dateOfUpload) {
        this.dateOfUpload = dateOfUpload;
    }

    public Object getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Capsule getCapsule() {
        return capsule;
    }

    public void setCapsule(Capsule capsule) {
        this.capsule = capsule;
    }
}