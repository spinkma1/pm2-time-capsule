import React from 'react';
import { AppBar, Toolbar, Button, Container, Typography, Grid, Box } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const LandingPage = ({ setCurrentPage }) => {
    const navigate = useNavigate();
    return (
        <div className="min-h-screen flex flex-col">
            <AppBar position="static" color="transparent" elevation={0}>
                <Toolbar>
                    <Typography variant="h6" className="text-blue-900 flex-grow">
                        MemoryCapsule
                    </Typography>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => navigate('/login')}
                        className="mr-2"
                        sx={{ marginRight: 2 }} // Add space between buttons
                    >
                        Přihlásit
                    </Button>
                    <Button
                        variant="outlined"
                        color="primary"
                        onClick={() => navigate('/register')}
                    >
                        Vytvořit účet
                    </Button>
                </Toolbar>
            </AppBar>

            <main className="flex-grow">
                <section className="bg-blue-50 py-20">
                    <Container>
                        <Typography variant="h3" align="center" className="text-blue-900 mb-6">
                            Uchovejte své vzpomínky v čase
                        </Typography>
                        <Typography variant="h6" align="center" className="mb-8">
                            Vytvořte digitální časové kapsle a otevřete je, když nastane ten pravý okamžik.
                        </Typography>
                        <Box textAlign="center">
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={() => setCurrentPage('register')}
                                className="text-lg"
                                sx={{ marginTop: 2 }} // Add marginTop
                            >
                                Začít zdarma
                            </Button>
                        </Box>
                    </Container>
                </section>

                <section className="py-16">
                    <Container>
                        <Typography variant="h4" align="center" className="mb-12">
                            Jak to funguje
                        </Typography>
                        <Grid container spacing={4}>
                            {[
                                { title: 'Vytvořte', description: 'Nahrajte fotky, videa nebo zprávy do vaší kapsle.' },
                                { title: 'Nastavte', description: 'Zvolte datum, kdy se kapsle otevře.' },
                                { title: 'Sdílejte', description: 'Pozvěte přátele a rodinu, aby přispěli do vaší kapsle.' }
                            ].map((step, index) => (
                                <Grid item xs={12} md={4} key={index}>
                                    <Box textAlign="center">
                                        <Box className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                                            <Typography variant="h4" className="text-blue-900">{index + 1}</Typography>
                                        </Box>
                                        <Typography variant="h6" className="font-semibold mb-2">{step.title}</Typography>
                                        <Typography>{step.description}</Typography>
                                    </Box>
                                </Grid>
                            ))}
                        </Grid>
                    </Container>
                </section>

                <section className="bg-blue-900 text-white py-16">
                    <Container>
                        <Typography variant="h4" align="center" className="mb-8">
                            Připraveni začít svou cestu časem?
                        </Typography>
                        <Box textAlign="center">
                            <Button
                                variant="contained"
                                color="white"
                                onClick={() => setCurrentPage('create-capsule')}
                                sx={{ marginTop: 2, 
                                    backgroundColor: 'white',
                                    color: "#1e3a8a", }} 
                            >
                                Vytvořit kapsli nyní
                            </Button>
                        </Box>
                    </Container>
                </section>
            </main>

            <footer className="bg-gray-100 py-8">
                <Container>
                    <Typography align="center">
                        &copy; 2024 MemoryCapsule. Všechna práva vyhrazena.
                    </Typography>
                </Container>
            </footer>
        </div>
    );
};

export default LandingPage;

