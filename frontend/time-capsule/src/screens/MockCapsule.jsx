const mockCapsule = {
    id: "1",
    name: "Time Capsule 2025",
    description: "A capsule containing memories and files for the future.",
    createdDate: "2024-12-31T10:00:00.000Z",
    unlockTime: "2025-12-31T10:00:00.000Z",
    state: "editing",
    users: [
        { id: "101", name: "Alice", email: "alice@example.com" },
        { id: "102", name: "Bob", email: "bob@example.com" }
    ],
    content: [
        {
            id: "201",
            name: "Vacation Photo",
            dataType: "image",
            thumbnail: "https://via.placeholder.com/150",
            addedBy: "Alice",
            addedDate: "2024-11-30T12:00:00.000Z"
        },
        {
            id: "202",
            name: "Graduation Speech",
            dataType: "text",
            content: "This is the speech content...",
            addedBy: "Bob",
            addedDate: "2024-12-01T15:00:00.000Z"
        },
        {
            id: "203",
            name: "Favorite Song",
            dataType: "audio",
            fileUrl: "https://example.com/song.mp3",
            addedBy: "Alice",
            addedDate: "2024-11-28T18:30:00.000Z"
        },
        {
            id: "204",
            name: "Birthday Video",
            dataType: "video",
            fileUrl: "https://example.com/video.mp4",
            addedBy: "Bob",
            addedDate: "2024-12-02T20:45:00.000Z"
        }
    ],
    capsuleSize: 10,
};

export default mockCapsule;
