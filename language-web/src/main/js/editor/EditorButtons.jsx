import { observer } from 'mobx-react-lite';
import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import ButtonGroup from '@material-ui/core/ButtonGroup';
import Divider from '@material-ui/core/Divider';
import FormatListNumberedIcon from '@material-ui/icons/FormatListNumbered';
import RedoIcon from '@material-ui/icons/Redo';
import UndoIcon from '@material-ui/icons/Undo';
import ToggleButton from '@material-ui/lab/ToggleButton';

import { useRootStore } from '../RootStore';

const useStyles = makeStyles(theme => ({
  iconButton: {
    padding: 7,
    minWidth: 36,
    border: 0,
    color: theme.palette.text.primary,
    '&.MuiButtonGroup-groupedTextHorizontal': {
      borderRight: 0,
    },
  },
  divider: {
    margin: theme.spacing(0.5),
  }
}));

export default observer(() => {
  const editorStore = useRootStore().editorStore;
  const classes = useStyles();
  return (
    <>
      <ButtonGroup
        variant='text'
      >
        <Button
          disabled={!editorStore.canUndo}
          onClick={() => editorStore.undo()}
          className={classes.iconButton}
          aria-label='Undo'
        >
          <UndoIcon fontSize='small'/>
        </Button>
        <Button
          disabled={!editorStore.canRedo}
          onClick={() => editorStore.redo()}
          className={classes.iconButton}
          aria-label='Redo'
        >
          <RedoIcon fontSize='small'/>
        </Button>
      </ButtonGroup>
      <Divider
        flexItem
        orientation='vertical'
        className={classes.divider}
      />
      <ToggleButton
        selected={editorStore.showLineNumbers}
        onChange={() => editorStore.toggleLineNumbers()}
        size='small'
        className={classes.iconButton}
        aria-label='Show line numbers'
        value='show-line-numbers'
      >
        <FormatListNumberedIcon fontSize='small'/>
      </ToggleButton>
    </>
  );
});
