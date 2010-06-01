/*
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

package org.F11.scada.server.converter;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.F11.scada.WifeException;
import org.F11.scada.WifeUtilities;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.event.WifeCommand;

/**
 * Wife�ʐM���W���[����MC3E�R�}���h�����̎����N���X�B
 */
public class MC3E implements Converter {
    /** �W����FINS�R�}���h����M�p�P�b�g�ő�T�C�Y��\���萔�ł� */
    public static final int DEFAULT_MC3E_PACKET_MAX_SIZE = 960 * 2;

    /** MC3E�R�}���h �w�b�_�� */
    public static final int MC3E_COMMAND_HEADER_LENGTH = 11;

    /** MC3E�R�}���h �v���f�[�^�� */
    public static final int MC3E_COMMAND_REQDATA_LENGTH = 12;

    /** MC3E�R�}���h �I���R�[�h�� */
    public static final int MC3E_COMMAND_ENDCODE_LENGTH = 2;

    /** ��������� D �f�[�^���W�X�^(word)��\���܂��B */
    public static final Integer D_AREA = new Integer(0);

    /** ��������� M ���������[�G���A(bit)��\���܂��B */
    public static final Integer M_AREA = new Integer(1);

    /** ��������� X ���̓����[�G���A(bit)��\���܂��B */
    public static final Integer X_AREA = new Integer(2);

    /** ��������� Y �o�̓����[�G���A(bit)��\���܂��B */
    public static final Integer Y_AREA = new Integer(3);

    /** ��������� B �����N�����[�G���A(bit)��\���܂��B */
    public static final Integer B_AREA = new Integer(4);

    /** ��������� L ���b�`�����[�G���A(bit)��\���܂��B */
    public static final Integer L_AREA = new Integer(5);

    /** ��������� ZR �t�@�C�����W�X�^(word)��\���܂��B */
    public static final Integer R_AREA = new Integer(10);

    /** ��������� W �����N���W�X�^(word)��\���܂��B */
    public static final Integer W_AREA = new Integer(11);

    /** ��������� ���ꃊ���[�G���A(bit)��\���܂��B */
    public static final Integer SM_AREA = new Integer(90);

    /** �R�}���h��ʂ̃}�b�v */
    private static final Map memoryModeMap;
    static {
        memoryModeMap = new HashMap();
        memoryModeMap.put(D_AREA, new ComndKindWorddev(0xA8));
        memoryModeMap.put(M_AREA, new ComndKindBitdev(0x90));
        memoryModeMap.put(X_AREA, new ComndKindBitdev(0x9C));
        memoryModeMap.put(Y_AREA, new ComndKindBitdev(0x9D));
        memoryModeMap.put(B_AREA, new ComndKindBitdev(0xA0));
        memoryModeMap.put(L_AREA, new ComndKindBitdev(0x92));
        memoryModeMap.put(R_AREA, new ComndKindWorddev(0xB0));
        memoryModeMap.put(W_AREA, new ComndKindWorddev(0xB4));
        memoryModeMap.put(SM_AREA, new ComndKindBitdev(0x91));
    }

    /** �w�b�_�f�[�^ */
    private byte[] head = { (byte) 0x50, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0xff, (byte) 0x03, (byte) 0x00 };

    /** CPU�Ď��^�C�} */
    private byte[] cpu = { (byte) 0x00, (byte) 0x00 };

    /**
     * �f�t�H���g�R���X�g���N�^
     */
    public MC3E() {
    }

    /** ����ݒ肵�A���X�|���X�w�b�_��Ԃ��B */
    public byte[] setEnvironment(Environment device) {
        head[2] = (byte) device.getPlcNetNo();
        head[3] = (byte) device.getPlcNodeNo();
        cpu[0] = (byte) (device.getPlcWatchWait() % 0x100);
        cpu[1] = (byte) (device.getPlcWatchWait() / 0x100);

        byte[] resp = new byte[head.length];
        resp[0] = (byte) 0xd0;
        resp[1] = (byte) 0x00;
        resp[2] = head[2];
        resp[3] = head[3];
        resp[4] = head[4];
        resp[5] = head[5];
        resp[6] = head[6];
        return resp;
    }

    /** �Ǎ��݃R�}���h��ݒ肷��B */
    public void setReadCommand(WifeCommand commdef) throws WifeException {
        mc3eCommand = READ_MC3ECOMMAND;
        mc3eCommand.setCommand(commdef, cpu, null);
    }

    /** �����݃R�}���h��ݒ肷��B */
    public void setWriteCommand(WifeCommand commdef, byte[] data)
            throws WifeException {
        mc3eCommand = WRITE_MC3ECOMMAND;
        mc3eCommand.setCommand(commdef, cpu, data);
    }

    /** �R�}���h���擾�\���H */
    public boolean hasCommand() {
        return mc3eCommand.hasCommand();
    }

    /** �R�}���h���쐬���A����̃R�}���h���������܂��B */
    public void nextCommand(ByteBuffer sendBuffer) {
        sendBuffer.put(head);
        mc3eCommand.nextCommand(sendBuffer);
    }

    /** �O����s�R�}���h���쐬���܂��B */
    public void retryCommand(ByteBuffer sendBuffer) {
        sendBuffer.put(head);
        mc3eCommand.retryCommand(sendBuffer);
    }

    /** ���M�f�[�^�Ǝ�M�f�[�^�̐��������������܂��B */
    public WifeException checkCommandResponce(ByteBuffer recvBuffer)
            throws WifeException {
        byte[] err = { 0, 0 };
        if (recvBuffer.remaining() < MC3E_COMMAND_HEADER_LENGTH) {
            StringBuffer sb = new StringBuffer();
            sb.append("RecvData (");
            sb.append(WifeUtilities.toString(recvBuffer));
            sb.append(") is short!");
            return new WifeException(WifeException.WIFE_ERROR,
                    WifeException.WIFE_NET_RESPONCE_ERROR, sb.toString()
                            + mc3eCommand.toString());
        }
        // �I���R�[�h
        if (recvBuffer.get(9) != (byte) 0x00
                || recvBuffer.get(10) != (byte) 0x00) {
            StringBuffer sb = new StringBuffer();
            sb.append("End code error: ");
            sb.append(" RecvData (");
            sb.append(WifeUtilities.toString(recvBuffer));
            sb.append(")");
            return new WifeException(WifeException.WIFE_ERROR,
                    WifeException.WIFE_NET_RESPONCE_ENDCODE_ERROR, err, sb
                            .toString()
                            + mc3eCommand.toString());
        }
        // �����f�[�^��
        int actualDataLen = recvBuffer.remaining() - MC3E_COMMAND_HEADER_LENGTH
                + MC3E_COMMAND_ENDCODE_LENGTH;
        int headerDataLen = ((int) recvBuffer.get(7) & 0x00ff)
                + (((int) recvBuffer.get(8) * 0x100) & 0x00ff00);
        if (actualDataLen != headerDataLen) {
            StringBuffer sb = new StringBuffer();
            sb.append("Header data length ").append(headerDataLen);
            sb.append(" != Actual data length ").append(actualDataLen);
            sb.append(" RecvData (");
            sb.append(WifeUtilities.toString(recvBuffer));
            sb.append(")");
            return new WifeException(WifeException.WIFE_ERROR,
                    WifeException.WIFE_NET_RESPONCE_CMND_ERROR, sb.toString());
        }
        int reqDataLen = mc3eCommand.getReqDataLength();
        if (actualDataLen - 2 != reqDataLen) {
            StringBuffer sb = new StringBuffer();
            sb.append("Request data length ").append(reqDataLen);
            sb.append(" != Actual data length ").append(actualDataLen);
            sb.append(" RecvData (");
            sb.append(WifeUtilities.toString(recvBuffer));
            sb.append(")");
            return new WifeException(WifeException.WIFE_ERROR,
                    WifeException.WIFE_NET_RESPONCE_CMND_ERROR, sb.toString());
        }
        return null;
    }

    /** ��M�f�[�^����f�[�^�����擾���܂��B */
    public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
        mc3eCommand.getResponceData(recvBuffer, recvData);
    }

    /** �ʐM���̍ő咷��Ԃ��܂��B */
    public int getPacketMaxSize(WifeCommand commdef) {
        return DEFAULT_MC3E_PACKET_MAX_SIZE / 2;
    }

    /**
     * ������\����Ԃ��܂��B
     */
    public String toString() {
        return mc3eCommand.toString();
    }

    /**
     * �R�}���h�ϊ��w���p�[�N���X�̃C���^�[�t�F�C�X�ł��B
     */
    private interface Mc3eCommand {
        /** ��������R�}���h��ݒ肷��B */
        public void setCommand(WifeCommand commdef, byte[] cpu, byte[] data)
                throws WifeException;

        /** �R�}���h���擾�\���H */
        public boolean hasCommand();

        /** �R�}���h���쐬���A����̃R�}���h���������܂��B */
        public void nextCommand(ByteBuffer sendBuffer);

        /** �O����s�R�}���h���쐬���܂��B */
        public void retryCommand(ByteBuffer sendBuffer);

        /** �R�}���h�̍���v���������擾���܂��B */
        public int getReqDataLength();

        /** ��M�f�[�^����f�[�^�����擾���܂��B */
        public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData);
    }

    /**
     * �Ǎ��݃R�}���h�ϊ��w���p�[�N���X�̃C���X�^���X�ł��B
     */
    private final Mc3eCommand READ_MC3ECOMMAND = new Mc3eCommand() {
        /** CPU�Ď��^�C�} */
        private byte[] cpu;

        /** PLC��������� */
        private byte memoryMode;

        /** PLC�������A�h���X */
        private long memoryAddress;

        /** �������ׂ�PLC�f�[�^�̎c��o�C�g�� */
        private int restByteLength = 0;

        /** �����PLC�f�[�^�o�C�g�� */
        private int thisByteLength = 0;

        /** �f�o�C�X��ʃw���p�[�N���X�ւ̎Q�� */
        private ComndKind kind;

        /** ��������R�}���h�̃l�^��ݒ肷��B */
        public void setCommand(WifeCommand commdef, byte[] cpu, byte[] data)
                throws WifeException {
            this.cpu = cpu;
            // �f�o�C�X��ʃw���p�[�N���X
            kind = (ComndKind) memoryModeMap.get(new Integer(commdef
                    .getMemoryMode()));
            if (kind == null) {
                throw new WifeException(WifeException.WIFE_ERROR,
                        WifeException.WIFE_NET_COMMAND_ERROR,
                        "Not supported memory mode " + commdef.getMemoryMode());
            }
            memoryMode = kind.getMemKind();

            memoryAddress = commdef.getMemoryAddress();
            restByteLength = commdef.getWordLength() * 2;
            thisByteLength = 0;
        }

        /** �R�}���h���擾�\���H */
        public boolean hasCommand() {
            return 0 < restByteLength;
        }

        /** �R�}���h���쐬���A����̃R�}���h���������܂��B */
        public void nextCommand(ByteBuffer sendBuffer) {
            long addr = kind.getAddress(memoryAddress);

            // �f�o�C�X�Ɉˑ����钷���𒴂��Ă����
            if (DEFAULT_MC3E_PACKET_MAX_SIZE < restByteLength) {
                thisByteLength = DEFAULT_MC3E_PACKET_MAX_SIZE;
                restByteLength -= DEFAULT_MC3E_PACKET_MAX_SIZE;
            } else {
                thisByteLength = restByteLength;
                restByteLength = 0;
            }

            // MC3E�w�b�_�̍쐬
            int datalen = MC3E_COMMAND_REQDATA_LENGTH;
            sendBuffer.put((byte) (datalen % 0x100));
            sendBuffer.put((byte) (datalen / 0x100));
            sendBuffer.put(cpu);
            // �R�}���h�A�T�u�R�}���h
            sendBuffer.put((byte) 0x01);
            sendBuffer.put((byte) 0x04);
            sendBuffer.put((byte) 0x00);
            sendBuffer.put((byte) 0x00);
            // �f�o�C�X�A�h���X
            sendBuffer.put((byte) (addr % 0x100));
            sendBuffer.put((byte) ((addr / 0x100) % 0x100));
            sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
            // �f�o�C�X�R�[�h
            sendBuffer.put(memoryMode);
            // ����
            sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
            sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));

            // ����̃A�h���X
            memoryAddress += (thisByteLength / 2);
        }

        /** �O����s�R�}���h���쐬���܂��B */
        public void retryCommand(ByteBuffer sendBuffer) {
            long addr = kind.getAddress(memoryAddress - (thisByteLength / 2));

            // MC3E�w�b�_�̍쐬
            int datalen = MC3E_COMMAND_REQDATA_LENGTH;
            sendBuffer.put((byte) (datalen % 0x100));
            sendBuffer.put((byte) (datalen / 0x100));
            sendBuffer.put(cpu);
            // �R�}���h�A�T�u�R�}���h
            sendBuffer.put((byte) 0x01);
            sendBuffer.put((byte) 0x04);
            sendBuffer.put((byte) 0x00);
            sendBuffer.put((byte) 0x00);
            // �f�o�C�X�A�h���X
            sendBuffer.put((byte) (addr % 0x100));
            sendBuffer.put((byte) ((addr / 0x100) % 0x100));
            sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
            // �f�o�C�X�R�[�h
            sendBuffer.put(memoryMode);
            // ����
            sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
            sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));
        }

        /** �R�}���h�̍���v���������擾���܂��B */
        public int getReqDataLength() {
            return thisByteLength;
        }

        /** ��M�f�[�^����f�[�^�����擾���܂��B */
        public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
            for (int i = MC3E_COMMAND_HEADER_LENGTH; i < recvBuffer.remaining(); i += 2) {
                recvData.put(recvBuffer.get(i + 1));
                recvData.put(recvBuffer.get(i + 0));
            }
        }

        /**
         * ���̃C���X�^���X�̕�����\����Ԃ��܂��B
         */
        public String toString() {
            StringBuffer s = new StringBuffer();
            s.append("ReadMode:").append("\n");
            s.append("memoryMode:").append(memoryMode).append("\n");
            s.append("memoryAddress:").append(memoryAddress).append("\n");
            s.append("restByteLength:").append(restByteLength).append("\n");
            s.append("thisByteLength:").append(thisByteLength).append("\n");
            return s.toString();
        }
    };

    /**
     * �����݃R�}���h�ϊ��w���p�[�N���X�̃C���X�^���X�ł��B
     */
    private final Mc3eCommand WRITE_MC3ECOMMAND = new Mc3eCommand() {
        /** CPU�Ď��^�C�} */
        private byte[] cpu;

        /** PLC��������� */
        private byte memoryMode;

        /** PLC�������A�h���X */
        private long memoryAddress;

        /** �����݃f�[�^ */
        private byte[] writeData;

        /** �����݃f�[�^�ʒu */
        private int writePos;

        /** �������ׂ�PLC�f�[�^�̎c��o�C�g�� */
        private int restByteLength = 0;

        /** �����PLC�f�[�^�o�C�g�� */
        private int thisByteLength = 0;

        /** �f�o�C�X��ʃw���p�[�N���X�ւ̎Q�� */
        private ComndKind kind;

        /** ��������R�}���h��ݒ肷��B */
        public void setCommand(WifeCommand commdef, byte[] cpu, byte[] data)
                throws WifeException {
            this.cpu = cpu;
            // �f�o�C�X��ʃw���p�[�N���X
            kind = (ComndKind) memoryModeMap.get(new Integer(commdef
                    .getMemoryMode()));
            if (kind == null) {
                throw new WifeException(WifeException.WIFE_ERROR,
                        WifeException.WIFE_NET_COMMAND_ERROR,
                        "Not supported memory mode " + commdef.getMemoryMode());
            }
            memoryMode = kind.getMemKind();
            memoryAddress = commdef.getMemoryAddress();
            writeData = data;
            writePos = 0;
            restByteLength = commdef.getWordLength() * 2;
            thisByteLength = 0;
        }

        /** �R�}���h���擾�\���H */
        public boolean hasCommand() {
            return 0 < restByteLength;
        }

        /** �R�}���h���쐬���A����̃R�}���h���������܂��B */
        public void nextCommand(ByteBuffer sendBuffer) {
            long addr = kind.getAddress(memoryAddress);

            // �f�o�C�X�Ɉˑ����钷���𒴂��Ă����
            if (DEFAULT_MC3E_PACKET_MAX_SIZE < restByteLength) {
                thisByteLength = DEFAULT_MC3E_PACKET_MAX_SIZE;
                restByteLength -= DEFAULT_MC3E_PACKET_MAX_SIZE;
            } else {
                thisByteLength = restByteLength;
                restByteLength = 0;
            }

            // MC3E�w�b�_�̍쐬
            int datalen = MC3E_COMMAND_REQDATA_LENGTH + thisByteLength;
            sendBuffer.put((byte) (datalen % 0x100));
            sendBuffer.put((byte) (datalen / 0x100));
            sendBuffer.put(cpu);
            // �R�}���h�A�T�u�R�}���h
            sendBuffer.put((byte) 0x01);
            sendBuffer.put((byte) 0x14);
            sendBuffer.put((byte) 0x00);
            sendBuffer.put((byte) 0x00);
            // �f�o�C�X�A�h���X
            sendBuffer.put((byte) (addr % 0x100));
            sendBuffer.put((byte) ((addr / 0x100) % 0x100));
            sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
            // �f�o�C�X�R�[�h
            sendBuffer.put(memoryMode);
            // ����
            sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
            sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));
            // �����݃f�[�^
            for (int i = writePos; i < (writePos + thisByteLength); i += 2) {
                sendBuffer.put(writeData[i + 1]);
                sendBuffer.put(writeData[i + 0]);
            }

            // ����̃A�h���X
            memoryAddress += (thisByteLength / 2);
            writePos += thisByteLength;
        }

        /** �O����s�R�}���h���쐬���܂��B */
        public void retryCommand(ByteBuffer sendBuffer) {
            long addr = kind.getAddress(memoryAddress - (thisByteLength / 2));
            int pos = writePos - thisByteLength;

            // MC3E�w�b�_�̍쐬
            int datalen = MC3E_COMMAND_REQDATA_LENGTH + thisByteLength;
            sendBuffer.put((byte) (datalen % 0x100));
            sendBuffer.put((byte) (datalen / 0x100));
            sendBuffer.put(cpu);
            // �R�}���h�A�T�u�R�}���h
            sendBuffer.put((byte) 0x01);
            sendBuffer.put((byte) 0x14);
            sendBuffer.put((byte) 0x00);
            sendBuffer.put((byte) 0x00);
            // �f�o�C�X�A�h���X
            sendBuffer.put((byte) (addr % 0x100));
            sendBuffer.put((byte) ((addr / 0x100) % 0x100));
            sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
            // �f�o�C�X�R�[�h
            sendBuffer.put(memoryMode);
            // ����
            sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
            sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));
            // �����݃f�[�^
            for (int i = pos; i < (pos + thisByteLength); i += 2) {
                sendBuffer.put(writeData[i + 1]);
                sendBuffer.put(writeData[i + 0]);
            }
        }

        /** �R�}���h�̍���v���������擾���܂��B */
        public int getReqDataLength() {
            return 0;
        }

        /** ��M�f�[�^����f�[�^�����擾���܂��B */
        public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
        }

        /**
         * ���̃C���X�^���X�̕�����\����Ԃ��܂��B
         */
        public String toString() {
            StringBuffer s = new StringBuffer();
            s.append("WriteMode:").append("\n");
            s.append("memoryMode:").append(memoryMode).append("\n");
            s.append("memoryAddress:").append(memoryAddress).append("\n");
            s.append("restByteLength:").append(restByteLength).append("\n");
            s.append("thisByteLength:").append(thisByteLength).append("\n");
            return s.toString();
        }
    };

    /** �R�}���h�ϊ��N���X */
    private Mc3eCommand mc3eCommand = READ_MC3ECOMMAND;

    /**
     * �f�o�C�X��ʃw���p�[�N���X
     */
    private abstract static class ComndKind {
        private byte memKind;

        public ComndKind(int kd) {
            this.memKind = (byte) (kd & 0xff);
        }

        public byte getMemKind() {
            return memKind;
        }

        abstract public long getAddress(long addr);
    }

    /**
     * �r�b�g�f�o�C�X���
     */
    private static class ComndKindBitdev extends ComndKind {
        public ComndKindBitdev(int kd) {
            super(kd);
        }

        public long getAddress(long addr) {
            return addr * 16;
        }
    }

    /**
     * ���[�h�f�o�C�X���
     */
    private static class ComndKindWorddev extends ComndKind {
        public ComndKindWorddev(int kd) {
            super(kd);
        }

        public long getAddress(long addr) {
            return addr;
        }
    }

}
