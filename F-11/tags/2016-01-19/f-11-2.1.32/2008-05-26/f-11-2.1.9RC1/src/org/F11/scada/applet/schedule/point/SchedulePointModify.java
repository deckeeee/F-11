/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002 Freedom, Inc. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.F11.scada.applet.schedule.point;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jp.gr.javacons.jim.Manager;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.dialog.GraphicScheduleViewDialog;
import org.F11.scada.applet.schedule.GraphicScheduleViewCreator;
import org.F11.scada.applet.schedule.ScheduleModel;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.security.AccessControlable;
import org.F11.scada.security.auth.Subject;
import org.F11.scada.server.schedule.point.dto.ScheduleGroupDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.util.RmiErrorUtil;
import org.F11.scada.xwife.applet.WifeDataProviderProxy;
import org.apache.log4j.Logger;

public class SchedulePointModify extends JDialog {
	private static final long serialVersionUID = -4004234865211877232L;
	private final Logger logger = Logger.getLogger(SchedulePointModify.class);
	private final SchedulePointTableModel model;
	private final int row;
	private final SchedulePointRowDto dto;
	private SchedulePointRowDto dtoOld;

	public SchedulePointModify(
			JDialog dialog,
			SchedulePointTableModel model,
			int row,
			boolean isSeparateSchedule,
			String pageId) {
		super(dialog, "スケジュールNo.変更", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.model = model;
		this.row = row;
		this.dto = model.getSchedulePointRowDto(row);
		init(dialog, isSeparateSchedule, pageId);
		setSize(520, 200);
		WifeUtilities.setCenter(this);
	}

	private void init(JDialog dialog, boolean isSeparateSchedule, String pageId) {
		setResizable(false);
		setLabels(dialog, isSeparateSchedule, pageId);
		setButtonPanel();
	}

	private void setLabels(
			JDialog dialog,
			boolean isSeparateSchedule,
			String pageId) {
		JPanel mainPanel = new JPanel(new GridLayout(4, 1, 0, 0));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel nameTitleLabel = new JLabel("機器名称：");
		mainPanel.add(nameTitleLabel);

		mainPanel.add(getKikiBox(isSeparateSchedule));

		JLabel scheNoTitleLabel = new JLabel("スケジュールNo.：");
		mainPanel.add(scheNoTitleLabel);

		mainPanel.add(getScheduleBox(dialog, pageId));

		getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	private Box getKikiBox(boolean isSeparateSchedule) {
		Box kikiBox = Box.createHorizontalBox();
		kikiBox.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		JLabel nameLabel = new JLabel(dto.getName());
		setMainLabel(nameLabel);
		setComponentSize(nameLabel, 380, 20);
		kikiBox.add(nameLabel);

		kikiBox.add(Box.createHorizontalStrut(5));

		if (isSeparateSchedule) {
			kikiBox.add(getSortComp(dto));
		}
		return kikiBox;
	}

	private JComponent getSortComp(SchedulePointRowDto rowDto) {
		if (isSort()) {
			JButton sortButton = new JButton("個別...");
			sortButton.addActionListener(new SeparateSchedule(dto, this));
			return sortButton;
		} else {
			JLabel sortComp = new JLabel("無し");
			sortComp.setHorizontalAlignment(SwingConstants.CENTER);
			setMainLabel(sortComp);
			setComponentSize(sortComp, 60, 20);
			return sortComp;
		}
	}

	private boolean isSort() {
		return dto.getSort().booleanValue();
	}

	private Box getScheduleBox(final JDialog dialog, final String pageId) {
		Box scheBox = Box.createHorizontalBox();
		scheBox.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		final JLabel scheNoLabel = new JLabel(dto.getGroupNo().toString());
		setMainLabel(scheNoLabel);
		setComponentSize(scheNoLabel, 40, 20);
		scheNoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scheBox.add(scheNoLabel);

		scheBox.add(Box.createHorizontalStrut(5));

		final JLabel scheName = new JLabel(dto.getGroupName());
		setMainLabel(scheName);
		setComponentSize(scheName, 335, 20);
		scheBox.add(scheName);

		scheBox.add(Box.createHorizontalStrut(5));

		JButton selectButton = new JButton("選択...");
		selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScheduleGroupSelect select =
					new ScheduleGroupSelect(dialog, getScheduleGroupDto(
						scheNoLabel,
						scheName), pageId);
				select.setVisible(true);
				setScheduleGroup(select, scheNoLabel, scheName);
			}

			private void setScheduleGroup(
					ScheduleGroupSelect select,
					JLabel scheNoLabel,
					JLabel scheName) {
				ScheduleGroupDto scheDto = select.getScheduleGroupDto();
				scheNoLabel.setText("" + scheDto.getGroupNo());
				scheName.setText(scheDto.getGroupName());
				copyDto();
				dto.setGroupNo(scheDto.getGroupNo());
				dto.setGroupName(scheDto.getGroupName());
			}

			private void copyDto() {
				if (null == dtoOld) {
					dtoOld = new SchedulePointRowDto();
				}
				dtoOld.setGroupNo(dto.getGroupNo());
				dtoOld.setGroupName(dto.getGroupName());
			}

			private ScheduleGroupDto getScheduleGroupDto(
					JLabel scheNoLabel,
					JLabel scheName) {
				ScheduleGroupDto groupDto = new ScheduleGroupDto();
				groupDto.setGroupNo(dto.getGroupNo());
				groupDto.setGroupName(dto.getGroupName());
				return groupDto;
			}
		});
		scheBox.add(selectButton);
		return scheBox;
	}

	private void setMainLabel(JComponent comp) {
		comp.setBackground(ColorFactory.getColor("white"));
		comp.setOpaque(true);
		comp.setBorder(BorderFactory.createLoweredBevelBorder());
	}

	private void setComponentSize(JComponent comp, int width, int height) {
		Dimension d = new Dimension(width, height);
		comp.setPreferredSize(d);
		comp.setMinimumSize(d);
		comp.setMaximumSize(d);
	}

	private void setButtonPanel() {
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		buttonBox.add(Box.createHorizontalStrut(350));
		JButton modifyButton = new JButton("変更");
		modifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SchedulePointUtil.setLoggingField(dto);
				model.setSchedulePointRowDto(dto, row);
				dispose();
			}
		});
		JButton cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyDto();
				dispose();
			}

			private void copyDto() {
				if (null != dtoOld) {
					dto.setGroupNo(dtoOld.getGroupNo());
					dto.setGroupName(dtoOld.getGroupName());
				}
			}
		});
		buttonBox.add(modifyButton);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(cancelButton);

		getContentPane().add(buttonBox, BorderLayout.SOUTH);
	}

	private static class SeparateSchedule implements ActionListener {
		private final Logger logger = Logger.getLogger(SeparateSchedule.class);
		private final SchedulePointRowDto rowDto;
		private final JDialog dialog;
		private final CheckPermissionUtil util;

		public SeparateSchedule(SchedulePointRowDto rowDto, JDialog dialog) {
			this.rowDto = rowDto;
			this.dialog = dialog;
			util = new CheckPermissionUtil(dialog);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				if (util.checkPermission(rowDto)) {
					SeparateScheduleDialog schedule =
						new SeparateScheduleDialog(dialog, rowDto);
					schedule.setVisible(true);
				}
			} catch (RemoteException ex) {
				RmiErrorUtil.error(logger, ex, dialog);
			}
		}

		private static class SeparateScheduleDialog extends JDialog {
			private static final long serialVersionUID = 4439734054103881294L;

			SeparateScheduleDialog(JDialog dialog, SchedulePointRowDto rowDto)
					throws RemoteException {
				super(dialog, rowDto.getName(), true);
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				Container container = getContentPane();
				container.add(
					getScheduleComp(dialog, rowDto),
					BorderLayout.CENTER);
				container.add(getCloseButton(), BorderLayout.SOUTH);
				pack();
				WifeUtilities.setCenter(this);
			}

			private Component getScheduleComp(
					JDialog dialog,
					SchedulePointRowDto rowDto) throws RemoteException {
				ScheduleModel model = new SeparateScheduleModel(rowDto, dialog);
				GraphicScheduleViewDialog view =
					new GraphicScheduleViewDialog(dialog, false, model
						.getTopSize(), true);
				GraphicScheduleViewCreator creator =
					view.createView(model, false, true);
				JComponent comp = creator.createView();
				comp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				return comp;
			}

			private Component getCloseButton() {
				Box box = Box.createHorizontalBox();
				box.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
				JButton closeButton = new JButton("閉じる");
				closeButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				box.add(Box.createHorizontalGlue());
				box.add(closeButton);
				return box;
			}
		}
	}

	private static class CheckPermissionUtil {
		private final Logger logger =
			Logger.getLogger(CheckPermissionUtil.class);
		private AccessControlable controlable;
		private final JDialog dialog;

		public CheckPermissionUtil(JDialog dialog) {
			this.dialog = dialog;
			try {
				controlable =
					(AccessControlable) Naming.lookup(WifeUtilities
						.createRmiActionControl());
			} catch (MalformedURLException e) {
				RmiErrorUtil.error(logger, e, dialog);
			} catch (RemoteException e) {
				RmiErrorUtil.error(logger, e, dialog);
			} catch (NotBoundException e) {
				RmiErrorUtil.error(logger, e, dialog);
			}
		}

		boolean checkPermission(SchedulePointRowDto rowDto) {
			Manager manager = Manager.getInstance();
			WifeDataProviderProxy proxy =
				(WifeDataProviderProxy) manager.getDataProvider(rowDto
					.getSeparateProvider());
			Subject subject = proxy.getSubject();
			try {
				List ret =
					controlable.checkPermission(
						subject,
						new String[][] { { getHolderId(rowDto) } });
				Boolean[] b = (Boolean[]) ret.get(0);
				return b[0].booleanValue();
			} catch (RemoteException e) {
				RmiErrorUtil.error(logger, e, dialog);
				return false;
			}
		}

		private String getHolderId(SchedulePointRowDto dto) {
			return dto.getSeparateProvider() + "_" + dto.getSeparateHolder();
		}
	}
}
