# Chess Game with AI Integration

## Overview

This is a chess game project that offers multiple gameplay modes with AI integration using LLM (Gemini) and Stockfish engine.

## Game Modes

- **AI vs AI** *(Coming in future update)*
- **Man vs AI** *(Currently has bugs but playable)*
- **Man vs Man** *(Available in separate repository)*

## Technical Architecture

### Backend Logic

The game uses a hybrid AI approach for black piece movements:

- **Gemini LLM**: Generates potential chess moves
- **Stockfish Engine**: Evaluates and scores the moves suggested by Gemini
- **Selection Process**: The highest-scoring move from Stockfish evaluation is chosen

### Current Limitations

⚠️ **Known Issues:**

- Black pieces occasionally overlap with white pieces
- Missing proper attacking logic
- Insufficient chess move patterns in the training data

**Root Cause:** The Gemini model is not fine-tuned with the custom moveset logic implemented in this game, leading to inconsistent behavior with standard chess rules.

## Future Development

### Planned Improvements

The project is **currently on hold** pending the following enhancement:

- **Model Fine-tuning**: Instead of providing built-in chess logic to Gemini, the plan is to fine-tune the model with custom chess logic
- **Role Adjustment**: Stockfish will be used purely for move scoring, while Gemini will handle move generation with proper understanding of the game's custom logic

## Installation & Setup

### Prerequisites

1. Install Stockfish chess engine
2. Update directory permissions for Stockfish
3. Configure the Stockfish path in the code to match your installation

```bash
# Change Stockfish directory permissions (Linux/Mac)
chmod +x /path/to/stockfish

# Update the path in your code configuration
STOCKFISH_PATH = "/your/specific/path/to/stockfish"
```

## Important Notes

⚠️ **Version Control**: 
- Please maintain all versions as-is
- Do not modify core game logic as it may prevent the game from running properly
- Feel free to fork and experiment, but keep the original versions intact

## Contributing

If you're interested in building upon this project:

1. You're welcome to copy and modify the code
2. Please preserve the original version structure
3. Document any significant changes you make

## Support & Contact

For installation help, bugs, or general questions:

📱 **Phone/WhatsApp:** +91 9907025510  
📧 **Email:** sarkaranurag556@gmail.com

Feel free to reach out for:
- Installation guidance
- Troubleshooting
- Technical discussions
- Collaboration opportunities

## License

*Add your license information here*

## Acknowledgments

- Stockfish chess engine
- Google Gemini LLM
- *Add other acknowledgments*

---

**Status:** 🚧 On Hold (Awaiting model fine-tuning capabilities)
