import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_617e16870bb4956d0e0afb3d0b193234
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonObject.mapYfJCBType.get(mapRow.get("TYPE"));

    }
}
